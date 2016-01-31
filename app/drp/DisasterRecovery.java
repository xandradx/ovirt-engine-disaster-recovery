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
import org.ovirt.engine.sdk.exceptions.ServerException;
import play.Logger;
import play.Play;
import play.i18n.Messages;
import play.libs.F;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jandrad on 23/01/16.
 */
public class DisasterRecovery {

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
            finishOperation(Messages.get("drp.alreadystarted"));
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
            reportMessage(Messages.get("drp.starting",  Messages.get(type)));

            reportMessage(Messages.get("drp.connectingapi"));
            api = OvirtApi.getApi();

            performOperation();

            saveOperationWithStatus(DisasterRecoveryOperation.OperationStatus.SUCCESS);

        } catch (Exception e) {
            reportError(e, e.getMessage());
            saveOperationWithStatus(DisasterRecoveryOperation.OperationStatus.FAILED);
            Logger.error(e, "Error in operation");

        } finally {
            if (api!=null) {
                api.shutdown();
            }

            finishOperation(Messages.get("drp.finished"));
        }
    }

    private void performOperation() throws Exception {

        reportMessage(Messages.get("drp.fetchinghosts"));
        Hosts hosts = api.getHosts();

        DisasterRecoveryDefinition definition = getDefinition(hosts.list(), type);

        if (definition.getLocalHosts().isEmpty() || definition.getRemoteHosts().isEmpty()) {
            reportError(Messages.get("drp.nohostsconfigured"));
        } else {
            List<Host> powerManagementHosts = new ArrayList<Host>();
            for (Host localHost : definition.getLocalHosts()) {

                if (DisasterRecoveryActions.hasPowerManagement(localHost)) {
                    reportMessage(Messages.get("drp.disablingpm", localHost.getName()));
                    DisasterRecoveryActions.disablePowerManagement(localHost);
                    powerManagementHosts.add(localHost);
                }

                String status = localHost.getStatus().getState();
                if ("non_responsive".equals(status)) {
                    reportMessage(Messages.get("drp.fencinghost",localHost.getName()));
                    DisasterRecoveryActions.fenceHost(localHost);
                    reportMessage(Messages.get("drp.deactivatinghost", localHost.getName()));
                    DisasterRecoveryActions.deactivateHost(localHost);
                }
            }

            reportMessage(Messages.get("drp.updatingdbconnections"));
            updateDatabase(type);

            for (Host remoteHost : definition.getRemoteHosts()) {

                if (DisasterRecoveryActions.hasPowerManagement(remoteHost)) {
                    reportMessage(Messages.get("drp.disablingpm", remoteHost.getName()));
                    DisasterRecoveryActions.disablePowerManagement(remoteHost);
                    powerManagementHosts.add(remoteHost);
                }

                reportMessage(Messages.get("drp.activatinghost", remoteHost.getName()));
                DisasterRecoveryActions.activateHost(remoteHost);
            }

            reportMessage(Messages.get("drp.waitingactivehost"));
            for (Host remoteHost : definition.getRemoteHosts()) {
                waitForStatus("up", remoteHost, 20*1000);
            }


            for (Host localHost : powerManagementHosts) {
                reportMessage(Messages.get("drp.enablingpm", localHost.getName()));
                DisasterRecoveryActions.enablePowerManagement(localHost);
            }
        }

    }

    private void waitForStatus(String status, Host currentHost, long timeout) throws ServerException, IOException {
        long time = 0;
        long initialMillis = System.currentTimeMillis();

        Host updatedHost;
        boolean hasExpectedStatus = false;
        do {
            updatedHost = api.getHosts().get(currentHost.getName());
            hasExpectedStatus = status.equals(updatedHost.getStatus().getState());
            reportMessage(Messages.get("drp.hoststatus", updatedHost.getName(), Messages.get(updatedHost.getStatus().getState())));
            time = System.currentTimeMillis() - initialMillis;
        } while (!hasExpectedStatus && time < timeout);

        if (!hasExpectedStatus) {
            reportError(Messages.get("drp.status.invalid", currentHost.getName()));
        } else {
            reportMessage(Messages.get("drp.status.valid", currentHost.getName()));
        }
    }

    private DisasterRecoveryDefinition getDefinition(List<Host> hostList, RemoteHost.RecoveryType type) throws HostsConditionsException {

        reportMessage(Messages.get("drp.verifyinghosts"));
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
                reportMessage(Messages.get("drp.originhost", host.getName(), Messages.get(state)));
                if (!"up".equalsIgnoreCase(state)) {
                    originNotUp++;
                } else {
                    originUp++;
                }
            }

            for (Host host : definition.getRemoteHosts()) {
                String state = host.getStatus().getState();
                reportMessage(Messages.get("drp.destinationhost", host.getName(), Messages.get(state)));
                if ("maintenance".equalsIgnoreCase(state)) {
                    destinationMaintenance++;
                }
            }

            if (originUp > 0) {
                throw new HostsConditionsException(Messages.get("drp.hostsmaintenancerequired"));
            }

            if (originNotUp > 0) {
                if (destinationMaintenance != definition.getRemoteHosts().size()) {
                    throw new HostsConditionsException(Messages.get("drp.hostsnotready"));
                }
            }

            reportMessage(Messages.get("drp.hostsready"));

        } else {
            throw new HostsConditionsException(Messages.get("drp.nohosts"));
        }

        return definition;
    }

    private void updateDatabase(RemoteHost.RecoveryType type) throws ConnectionUpdateException{

        List<DatabaseConnection> connections = DatabaseConnection.find("active = ?", true).fetch();
        List<DatabaseIQN> iqns = DatabaseIQN.find("active = ?", true).fetch();

        String dbHost = Play.configuration.getProperty("ovirt.db.host");
        String dbPort = Play.configuration.getProperty("ovirt.db.port");
        String dbName = Play.configuration.getProperty("ovirt.db.name");
        String dbUser = Play.configuration.getProperty("ovirt.db.user");
        String dbPassword = Play.configuration.getProperty("ovirt.db.password");

        if (dbHost==null || dbPort == null || dbName == null || dbUser == null || dbPassword == null) {
            reportError(Messages.get("drp.nodbcredentials"));
        } else {
            DatabaseManager manager = new DatabaseManager(dbHost, dbPort, dbName, dbUser, dbPassword);
            DisasterRecoveryActions.updateConnections(manager, connections, iqns, type==RemoteHost.RecoveryType.FAILBACK, listener);
        }
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
