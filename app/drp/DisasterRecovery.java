/*
 *   Copyright 2016 ITM, S.A.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * `*   `[`http://www.apache.org/licenses/LICENSE-2.0`](http://www.apache.org/licenses/LICENSE-2.0)
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package drp;

import drp.exceptions.*;
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
import org.ovirt.engine.sdk.decorators.DataCenter;
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
            finishOperation(Messages.get("drp.alreadystarted"), false);
            return;
        }

        api = null;

        F.Promise<DisasterRecoveryOperation> promise = new OperationCreationJob(type).now();

        try {
            dbOperation = promise.get();
        } catch (Exception e) {
            Logger.error(e, "Could not get database operation");
        }

        DisasterRecoveryOperation.OperationStatus status = DisasterRecoveryOperation.OperationStatus.PROGRESS;
        try {
            reportInfo(Messages.get("drp.starting", Messages.get(type)));
            reportInfo(Messages.get("drp.connectingapi"));
            api = OvirtApi.getApi();
            reportSuccess(Messages.get("drp.connectingapisuccess"));
            performOperation();
            status = DisasterRecoveryOperation.OperationStatus.SUCCESS;
        } catch (DBConfigurationException dbe) {
            Logger.error(dbe, "Error in operation");
            reportError(dbe, Messages.get("drp.dbconfiguration.error"));
            status = DisasterRecoveryOperation.OperationStatus.FAILED;
        } catch (Exception e) {
            Logger.error(e, "Error in operation");
            reportError(e, e.getMessage());
            status = DisasterRecoveryOperation.OperationStatus.FAILED;
        } finally {
            if (api!=null) {
                api.shutdown();
            }

            saveOperationWithStatus(status);
            finishOperation(Messages.get("drp.finished", Messages.get(status.toString())), status == DisasterRecoveryOperation.OperationStatus.SUCCESS);
        }
    }

    private void performOperation() throws Exception {

        reportInfo(Messages.get("drp.fetchinghosts"));
        Hosts hosts = api.getHosts();
        reportSuccess(Messages.get("drp.fetchinghostssuccess"));

        DisasterRecoveryDefinition definition = getDefinition(hosts.list(), type);

        if (definition.getLocalHosts().isEmpty() || definition.getRemoteHosts().isEmpty()) {
            reportError(Messages.get("drp.nohostsconfigured"));
        } else {

            List<Host> powerManagementHosts = new ArrayList<Host>();

            //Disabling power management
            for (Host host : hosts.list()) {
                if (DisasterRecoveryActions.hasPowerManagement(host)) {
                    reportInfo(Messages.get("drp.disablingpm", host.getName()));
                    DisasterRecoveryActions.disablePowerManagement(host);
                    reportSuccess(Messages.get("drp.disablingpmsuccess", host.getName()));
                    powerManagementHosts.add(host);
                }
            }

            //Checking local hosts
            for (Host localHost : definition.getLocalHosts()) {
                String status = localHost.getStatus().getState();
                if ("non_responsive".equals(status)) {
                    reportInfo(Messages.get("drp.fencinghost",localHost.getName()));
                    DisasterRecoveryActions.fenceHost(localHost);
                    reportSuccess(Messages.get("drp.fencinghostsuccess", localHost.getName()));

                    reportInfo(Messages.get("drp.deactivatinghost", localHost.getName()));
                    DisasterRecoveryActions.deactivateHost(localHost);
                    reportSuccess(Messages.get("drp.deactivatinghostsuccess", localHost.getName()));
                }
            }

            reportInfo(Messages.get("drp.updatingdbconnections"));
            updateConnections(type);

            //Activating remote hosts
            for (Host remoteHost : definition.getRemoteHosts()) {
                reportInfo(Messages.get("drp.activatinghost", remoteHost.getName()));
                DisasterRecoveryActions.activateHost(remoteHost);
                reportSuccess(Messages.get("drp.activatinghostsuccess", remoteHost.getName()));
            }

            reportInfo(Messages.get("drp.waitingactivehost"));

            //Checking hosts status
            int upHosts = 0;
            for (Host remoteHost : definition.getRemoteHosts()) {
                if (waitForStatus("up", remoteHost, 120*1000)) {
                    upHosts++;
                }

                if ("up".equals(remoteHost.getStatus().getState())) {
                    listener.onRefreshHosts();
                }
            }

            if (upHosts!=definition.getRemoteHosts().size()) {
                throw new HostActivateException(Messages.get("drp.activateexceptionhosts"));
            }

            //Checking data centers status
            reportInfo(Messages.get("drp.waitingactivedatacenters"));
            int upDataCenters = 0;
            for (DataCenter dataCenter : api.getDataCenters().list()) {
                if (waitForStatus("up", dataCenter, 480*1000)) {
                    upDataCenters++;
                }

                if ("up".equals(dataCenter.getStatus().getState())) {
                    listener.onRefreshDatacenters();
                }
            }

            if (upHosts!=definition.getRemoteHosts().size()) {
                throw new HostActivateException(Messages.get("drp.activateexceptiondatacenters"));
            }

            //Enabling power management
            for (Host localHost : powerManagementHosts) {
                reportInfo(Messages.get("drp.enablingpm", localHost.getName()));
                DisasterRecoveryActions.enablePowerManagement(localHost);
                reportSuccess(Messages.get("drp.enablepmsuccess", localHost.getName()));
            }
        }

    }

    private boolean waitForStatus(String status, Host currentHost, long timeout) throws ServerException, IOException, InterruptedException {
        long time;
        long initialMillis = System.currentTimeMillis();

        String currentStatus = null;
        Host updatedHost;
        boolean hasExpectedStatus ;
        do {
            updatedHost = api.getHosts().get(currentHost.getName());

            String state = updatedHost.getStatus().getState();
            if (!state.equalsIgnoreCase(currentStatus)) {
                reportInfo(Messages.get("drp.hoststatus", updatedHost.getName(), Messages.get(state)));
            }

            currentStatus = state;
            hasExpectedStatus = status.equals(state);
            Thread.sleep(5000);
            time = System.currentTimeMillis() - initialMillis;
        } while (!hasExpectedStatus && time < timeout);

        if (!hasExpectedStatus) {
            reportError(Messages.get("drp.status.host.invalid", currentHost.getName()));
        } else {
            reportSuccess(Messages.get("drp.status.host.valid", currentHost.getName()));
        }

        return hasExpectedStatus;
    }

    private boolean waitForStatus(String status, DataCenter dataCenter, long timeout) throws ServerException, IOException, InterruptedException {
        long time;
        long initialMillis = System.currentTimeMillis();

        String currentStatus = null;
        DataCenter updatedDC;
        boolean hasExpectedStatus;
        do {
            updatedDC = api.getDataCenters().get(dataCenter.getName());
            String state = updatedDC.getStatus().getState();
            if (!state.equalsIgnoreCase(currentStatus)) {
                reportInfo(Messages.get("drp.datacenterstatus", updatedDC.getName(), Messages.get(state)));
            }

            currentStatus = state;
            hasExpectedStatus = status.equals(state);
            Thread.sleep(5000);
            time = System.currentTimeMillis() - initialMillis;
        } while (!hasExpectedStatus && time < timeout);

        if (!hasExpectedStatus) {
            reportError(Messages.get("drp.status.datacenter.invalid", updatedDC.getName()));
        } else {
            reportSuccess(Messages.get("drp.status.datacenter.valid", updatedDC.getName()));
        }

        return hasExpectedStatus;
    }

    private DisasterRecoveryDefinition getDefinition(List<Host> hostList, RemoteHost.RecoveryType type) throws HostsConditionsException {

        reportInfo(Messages.get("drp.verifyinghosts"));
        DisasterRecoveryDefinition definition = new DisasterRecoveryDefinition();

        List<String> remoteHosts = RemoteHost.find("SELECT h.hostName FROM RemoteHost h WHERE h.type = :t AND h.active = :a").bind("t", type).bind("a", true).fetch();
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
                reportInfo(Messages.get("drp.originhost", host.getName(), Messages.get(state)));
                if (!"up".equalsIgnoreCase(state)) {
                    originNotUp++;
                } else {
                    originUp++;
                }
            }

            for (Host host : definition.getRemoteHosts()) {
                String state = host.getStatus().getState();
                reportInfo(Messages.get("drp.destinationhost", host.getName(), Messages.get(state)));
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

            reportSuccess(Messages.get("drp.hostsready"));

        } else {
            throw new HostsConditionsException(Messages.get("drp.nohosts"));
        }

        return definition;
    }

    private void updateConnections(RemoteHost.RecoveryType type) throws ConnectionUpdateException {

        List<DatabaseConnection> connections = DatabaseConnection.find("active = :a").bind("a", true).fetch();
        List<DatabaseIQN> iqns = DatabaseIQN.find("active = :a").bind("a", true).fetch();

        DisasterRecoveryActions.updateConnections(api, connections, iqns, type==RemoteHost.RecoveryType.FAILBACK, listener);
    }

    private void reportInfo(String message) {

        if (dbOperation!=null) {
            dbOperation.addMessageLog(message);
        }
        listener.onMessage(null, message, OperationListener.MessageType.INFO);
    }

    private void reportSuccess(String message) {
        if (dbOperation!=null) {
            dbOperation.addMessageLog(message);
        }
        listener.onMessage(null, message, OperationListener.MessageType.SUCCESS);
    }

    private void reportError(Exception e, String message) {

        if (dbOperation!=null) {
            dbOperation.addErrorLog(message);
        }

        listener.onMessage(e, message, OperationListener.MessageType.ERROR);
    }

    private void reportError(String message) {
        reportError(null, message);
    }

    private void finishOperation(String message, boolean success) {
        listener.onFinished(message, success);
    }

    private void saveOperationWithStatus(DisasterRecoveryOperation.OperationStatus status) {
        if (dbOperation!=null) {
            new OperationUpdateJob(dbOperation.id, status).now();
        }
    }
}
