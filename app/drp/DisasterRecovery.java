package drp;

import drp.exceptions.ConnectionUpdateException;
import drp.exceptions.HostsConditionsException;
import drp.objects.DatabaseManager;
import drp.objects.DisasterRecoveryDefinition;
import drp.objects.OperationListener;
import jobs.OperationCreationJob;
import jobs.OperationUpdateJob;
import models.DatabaseConnection;
import models.DatabaseIQN;
import models.DisasterRecoveryOperation;
import models.RemoteHost;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.Host;
import org.ovirt.engine.sdk.decorators.Hosts;
import play.Logger;
import play.Play;
import play.i18n.Messages;
import play.jobs.Job;
import play.libs.F;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jandrad on 23/01/16.
 */
public class DisasterRecovery {

    private static String DB_HOST = "nrhevm.itmlabs.local";
    private static String DB_PORT = "5432";
    private static String DB_USER = "engine";
    private static String DB_PASSWORD = "cIJ1QATeRXioSY9ZHXDeaf";
    private static String DB_NAME = "engine";

    private RemoteHost.RecoveryType type;
    private OperationListener listener;
    private DisasterRecoveryOperation dbOperation;
    private Api api = null;

    public DisasterRecovery(RemoteHost.RecoveryType type, OperationListener listener) {
        this.type = type;
        this.listener = listener;
    }

    public void startOperation() {

        long pendingOperations = DisasterRecoveryOperation.count("status = ?", DisasterRecoveryOperation.OperationStatus.PROGRESS);
        if (pendingOperations > 0) {
            finishOperation(Messages.get("DRP operation already in progress"));
            return;
        }

        api = null;

        F.Promise<DisasterRecoveryOperation> promise = new OperationCreationJob(type).now();

        try {
            dbOperation = promise.get();
        } catch (Exception e) {
            Logger.error(e, "Could not get database operation");
        }


        try {
            reportMessage(Messages.get("Starting " + type));

            reportMessage(Messages.get("Connecting to manager..."));
            api = OvirtApi.getApi();

            performOperation();

            saveOperationWithStatus(DisasterRecoveryOperation.OperationStatus.SUCCESS);

        } catch (Exception e) {
            reportError(e, e.getMessage());
            saveOperationWithStatus(DisasterRecoveryOperation.OperationStatus.FAILED);

        } finally {
            if (api!=null) {
                api.shutdown();
            }

            finishOperation(Messages.get("Operation finished"));
        }
    }

    private void performOperation() throws Exception {

        reportMessage(Messages.get("Fetching hosts..."));
        Hosts hosts = api.getHosts();

        DisasterRecoveryDefinition definition = getDefinition(hosts.list(), type);

        if (definition.getLocalHosts().isEmpty() || definition.getRemoteHosts().isEmpty()) {
            reportError(Messages.get("No hosts configured"));
        } else {
            List<Host> powerManagementHosts = new ArrayList<Host>();
            for (Host localHost : definition.getLocalHosts()) {

                if (DisasterRecoveryActions.hasPowerManagement(localHost)) {
                    reportMessage(Messages.get("Disabling power management in host: ") + localHost.getName());
                    DisasterRecoveryActions.disablePowerManagement(localHost);
                    powerManagementHosts.add(localHost);
                }

                String status = localHost.getStatus().getState();
                if ("non_responsive".equals(status)) {
                    reportMessage(Messages.get("Fencing host: ") + localHost.getName());
                    DisasterRecoveryActions.fenceHost(localHost);
                    reportMessage(Messages.get("Deactivating host: ") + localHost.getName());
                    DisasterRecoveryActions.deactivateHost(localHost);
                }
            }

            reportMessage(Messages.get("Updating database connections"));
            updateDatabase(type);

            for (Host remoteHost : definition.getRemoteHosts()) {

                if (DisasterRecoveryActions.hasPowerManagement(remoteHost)) {
                    reportMessage(Messages.get("Disabling power management in host: ") + remoteHost.getName());
                    DisasterRecoveryActions.disablePowerManagement(remoteHost);
                    powerManagementHosts.add(remoteHost);
                }

                reportMessage(Messages.get("Activating host: ") + remoteHost.getName());
                DisasterRecoveryActions.activateHost(remoteHost);
            }

            //TODO: Check status until host is UP or timeout

            for (Host localHost : powerManagementHosts) {
                reportMessage(Messages.get("Enabling power management in host ") + localHost.getName());
                DisasterRecoveryActions.enablePowerManagement(localHost);
            }
        }

    }

    private DisasterRecoveryDefinition getDefinition(List<Host> hostList, RemoteHost.RecoveryType type) throws HostsConditionsException {

        reportMessage(Messages.get("Verifying hosts"));
        DisasterRecoveryDefinition definition = new DisasterRecoveryDefinition();

        List<String> remoteHosts = RemoteHost.find("SELECT h.hostName FROM RemoteHost h WHERE h.type = ? AND h.active = ?", type, true).fetch();
        if (!hostList.isEmpty()) {
            for (Host host : hostList) {
                if (remoteHosts.contains(host.getName())) {
                    definition.addRemoteHost(host);
                } else {
                    definition.addLocalHost(host);
                }
            }

            int originUp = 0;
            int originNotUp = 0;
            int destinationMaintenance = 0;

            for (Host host : definition.getLocalHosts()) {
                String state = host.getStatus().getState();
                reportMessage(Messages.get("Origin host (%s) status: %s", host.getName(), state));
                if (!"up".equalsIgnoreCase(state)) {
                    originNotUp++;
                } else {
                    originUp++;
                }
            }

            for (Host host : definition.getRemoteHosts()) {
                String state = host.getStatus().getState();
                reportMessage(Messages.get("Destination host (%s) status: %s", host.getName(), state));
                if ("maintenance".equalsIgnoreCase(state)) {
                    destinationMaintenance++;
                }
            }

            if (originUp > 0) {
                throw new HostsConditionsException(Messages.get("All Host must be in maintenance status, to start controlled DRP process"));
            }

            if (originNotUp > 0) {
                if (destinationMaintenance != definition.getRemoteHosts().size()) {
                    throw new HostsConditionsException(Messages.get("Remote hosts not ready for operation"));
                }
            }

            reportMessage(Messages.get("Hosts ready for controlled DRP process"));

        } else {
            throw new HostsConditionsException(Messages.get("No hosts available"));
        }

        return definition;
    }

    private void updateDatabase(RemoteHost.RecoveryType type) throws ConnectionUpdateException{

        List<DatabaseConnection> connections = DatabaseConnection.find("active = ?", true).fetch();
        List<DatabaseIQN> iqns = DatabaseIQN.find("active = ?", true).fetch();

        DatabaseManager manager = new DatabaseManager(DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD);
        DisasterRecoveryActions.updateConnections(manager, connections, iqns, type==RemoteHost.RecoveryType.FAILBACK, listener);
    }

    private void reportMessage(String message) {

        if (dbOperation!=null) {
            dbOperation.addMessageLog(message);
        }
        listener.onMessage(message);
    }

    private void reportError(Exception e, String message) {

        if (dbOperation!=null) {
            dbOperation.addErrorLog(message);
        }

        listener.onError(e, message);
    }

    private void reportError(String message) {
        reportError(null, message);
    }

    private void finishOperation(String message) {
        listener.onFinished(message);
    }

    private void saveOperationWithStatus(DisasterRecoveryOperation.OperationStatus status) {
        if (dbOperation!=null) {
            new OperationUpdateJob(dbOperation.id, status).now();
        }
    }
}
