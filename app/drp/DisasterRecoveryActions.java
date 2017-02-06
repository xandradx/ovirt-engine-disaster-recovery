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
import drp.objects.OperationListener;
import models.DatabaseConnection;
import models.DatabaseIQN;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.Host;
import org.ovirt.engine.sdk.entities.Action;
import org.ovirt.engine.sdk.entities.PowerManagement;
import org.ovirt.engine.sdk.entities.StorageConnection;
import org.ovirt.engine.sdk.exceptions.ServerException;
import play.Logger;
import play.i18n.Messages;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by jandrad on 23/01/16.
 */
public class DisasterRecoveryActions {

    public static void fenceHost(Host host) throws FencingException{

        if (host == null) {
            throw new FencingException("Invalid host");
        }

        try {
            Logger.debug("Fencing host: %s", host.getName());
            Action requestAction = new Action();
            requestAction.setFenceType("manual");
            Action resultAction = host.fence(requestAction);
            if (!"complete".equals(resultAction.getStatus().getState())) {
                throw new FencingException(Messages.get("drp.fencingexception",host.getName()));
            }
        } catch (Exception e) {
            throw new FencingException(Messages.get("drp.fencingexception",host.getName()));
        }
    }

    public static boolean hasPowerManagement(Host host) {
        if (host.isSetPowerManagement()) {
            return host.getPowerManagement().getEnabled();
        }

        return false;
    }

    public static void enablePowerManagement(Host host) throws ServerException, IOException {

        Logger.debug("Enabling power management host: %s", host.getName());

        PowerManagement powerManagement = host.getPowerManagement();
        if (powerManagement != null) {
            powerManagement.setEnabled(true);
            host.setPowerManagement(powerManagement);
            host.update();
        }
    }

    public static void disablePowerManagement(Host host) throws ServerException, IOException {

        Logger.debug("Disabling power management host: %s", host.getName());

        PowerManagement powerManagement = host.getPowerManagement();
        if (powerManagement != null) {
            powerManagement.setEnabled(false);
            host.setPowerManagement(powerManagement);
            host.update();
        }
    }

    public static void deactivateHost(Host host) throws HostDeactivateException{

        if (host == null) {
            throw new HostDeactivateException(Messages.get("drp.deactivateexceptioninvalidhost"));
        }

        try {
            Logger.debug("Deactivating host: %s", host.getName());
            Action requestAction = new Action();
            Action resultAction = host.deactivate(requestAction);
            if (!"complete".equals(resultAction.getStatus().getState())) {
                throw new HostDeactivateException(Messages.get("drp.deactivateexception",host.getName()));
            }
        } catch (Exception e) {
            throw new HostDeactivateException(Messages.get("drp.deactivateexception",host.getName()));
        }
    }

    public static void activateHost(Host host) throws HostActivateException{

        if (host == null) {
            throw new HostActivateException(Messages.get("drp.activateexceptioninvalidhost"));
        }

        try {
            Logger.debug("Activating host: %s", host.getName());
            Action requestAction = new Action();
            Action resultAction = host.activate(requestAction);
            if (!"complete".equals(resultAction.getStatus().getState())) {
                throw new HostActivateException(Messages.get("drp.activateexception", host.getName()));
            }
        } catch (Exception e) {
            throw new HostActivateException(Messages.get("drp.activateexception",host.getName()));
        }
    }

    public static void testConnection(DatabaseManager manager) throws DBConfigurationException {
        Connection connection = null;

        try {
            Class.forName("org.postgresql.Driver");
            String databaseURL = "jdbc:postgresql://" + manager.getDbHost() + ":" + manager.getDbPort() + "/" + manager.getDbName() + "";
            connection = DriverManager.getConnection(databaseURL, manager.getDbUser(), manager.getDbPassword());
        } catch (SQLException se) {
            Logger.error(se, "Error updating connections");
            throw new DBConfigurationException(Messages.get("drp.db.couldnotconnect"));
        } catch (ClassNotFoundException ce) {
            throw new DBConfigurationException(Messages.get("drp.db.nodriver"));
        } finally {
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    Logger.error(e, Messages.get("drp.db.couldnotdisconnect"));
                }
            }
        }
    }

    public static void updateConnections(Api api, List<DatabaseConnection> connections, List<DatabaseIQN> iqns, boolean revert, OperationListener listener) throws ConnectionUpdateException {
        if (connections.isEmpty() && iqns.isEmpty()) {
            throw new ConnectionUpdateException(Messages.get("drp.db.noconnections"));
        }

        listener.onMessage(null, Messages.get("drp.db.currentconnections"), OperationListener.MessageType.INFO);
        listConnections(api, listener);

        updateConnections(api, connections, revert);
        updateIQN(api, iqns, revert);

        listener.onMessage(null, Messages.get("drp.db.modifiedconnections"), OperationListener.MessageType.SUCCESS);
        listConnections(api, listener);
    }

    private static void listConnections(Api api, OperationListener listener) throws ConnectionUpdateException {
        try {
            api.getStorageConnections().list().forEach(storageConnection ->
                    listener.onMessage(null,
                            Messages.get("drp.db.connection",
                            storageConnection.getTarget(),
                            storageConnection.getAddress()),
                            OperationListener.MessageType.INFO));
        } catch (ServerException |  IOException e) {
            Logger.error(e, "Error listing storage connections");
            throw new ConnectionUpdateException(Messages.get("drp.couldnotlistconnections"));
        }
    }

    private static void updateConnections(Api api, List<DatabaseConnection> connections, boolean revert) throws ConnectionUpdateException {
        for (DatabaseConnection connection : connections) {
            try {
                String oldConnection = revert ? connection.destinationConnection : connection.originConnection;
                String newConnection = revert ? connection.originConnection : connection.destinationConnection;

                // Finding connections to update
                List<org.ovirt.engine.sdk.decorators.StorageConnection> connectionsToUpdate =
                        api.getStorageConnections().list().stream().filter(
                                storageConnection -> oldConnection.equals(storageConnection.getAddress()))
                                .collect(Collectors.toList());

                // Update connections
                connectionsToUpdate.forEach(storageConnection -> {
                    try {
                        storageConnection.setAddress(newConnection);
                        storageConnection.update(null, null, null, true);
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }
                });
            } catch (Exception e) {
                throw new ConnectionUpdateException(Messages.get("drp.updatingdbconnections.error"));
            }
        }
    }

    private static void updateIQN(Api api, List<DatabaseIQN> iqns, boolean revert) throws ConnectionUpdateException {
        for (DatabaseIQN iqn : iqns) {
            try {
                String oldIQN = revert ? iqn.destinationIQN : iqn.originIQN;
                String newIQN = revert ? iqn.originIQN : iqn.destinationIQN;

                // Finding connections to update
                List<org.ovirt.engine.sdk.decorators.StorageConnection> connectionsToUpdate =
                        api.getStorageConnections().list().stream().filter(
                                storageConnection -> oldIQN.equals(storageConnection.getTarget()))
                                .collect(Collectors.toList());

                // Update connections
                connectionsToUpdate.forEach(storageConnection -> {
                    try {
                        storageConnection.setTarget(newIQN);
                        storageConnection.update(null, null, null, true);
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }
                });
            } catch (Exception e) {
                throw new ConnectionUpdateException(Messages.get("drp.updatingdbconnections.error"));
            }
        }
    }
}
