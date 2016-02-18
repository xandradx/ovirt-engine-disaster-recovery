package drp;

import drp.exceptions.*;
import drp.objects.DatabaseManager;
import drp.objects.DisasterRecoveryDefinition;
import drp.objects.OperationListener;
import models.Configuration;
import models.DatabaseConnection;
import models.DatabaseIQN;
import models.RemoteHost;
import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.decorators.Host;
import org.ovirt.engine.sdk.entities.*;
import org.ovirt.engine.sdk.exceptions.ServerException;
import play.Logger;
import play.i18n.Messages;

import java.io.IOException;
import java.sql.*;
import java.util.List;

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

    public static void updateConnections(DatabaseManager manager, List<DatabaseConnection> connections, List<DatabaseIQN> iqns, boolean revert, OperationListener listener) throws ConnectionUpdateException, DBConfigurationException {

        if (connections.isEmpty() && iqns.isEmpty()) {
            throw new ConnectionUpdateException(Messages.get("drp.db.noconnections"));
        }

        Connection connection = null;

        try {
            Class.forName("org.postgresql.Driver");
            String databaseURL = "jdbc:postgresql://" + manager.getDbHost() + ":" + manager.getDbPort() + "/" + manager.getDbName() + "";
            connection = DriverManager.getConnection(databaseURL, manager.getDbUser(), manager.getDbPassword());

            listener.onMessage(null, Messages.get("drp.db.currentconnections"), OperationListener.MessageType.INFO);
            listConnections(connection, listener);

            updateConnections(connection, connections, revert, listener);
            updateIQN(connection, iqns, revert, listener);

            listener.onMessage(null, Messages.get("drp.db.modifiedconnections"), OperationListener.MessageType.SUCCESS);
            listConnections(connection, listener);

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
                    listener.onMessage(e, Messages.get("drp.db.couldnotdisconnect"), OperationListener.MessageType.ERROR);
                }
            }
        }
    }

    private static void listConnections(Connection dbConnection, OperationListener listener) throws SQLException{

        String query = "SELECT connection, iqn FROM storage_server_connections WHERE iqn IS NOT NULL;";
        Statement statement = null;

        try {
            statement = dbConnection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                listener.onMessage(null, Messages.get("drp.db.connection", resultSet.getString(2), resultSet.getString(1)), OperationListener.MessageType.INFO);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    private static void updateConnections(Connection dbConnection, List<DatabaseConnection> connections, boolean revert, OperationListener listener) throws SQLException {

        for (DatabaseConnection connection : connections) {

            PreparedStatement updateConnection = null;
            String query = "UPDATE storage_server_connections SET connection = ? WHERE connection = ? AND iqn IS NOT NULL;";

            try {
                dbConnection.setAutoCommit(false);
                updateConnection = dbConnection.prepareStatement(query);

                if (revert) {
                    updateConnection.setString(2, connection.destinationConnection);
                    updateConnection.setString(1, connection.originConnection);
                } else {
                    updateConnection.setString(1, connection.destinationConnection);
                    updateConnection.setString(2, connection.originConnection);
                }

                updateConnection.executeUpdate();
                dbConnection.commit();
            } catch (SQLException e) {
                if (dbConnection!=null) {
                    try {
                        dbConnection.rollback();
                    } catch (SQLException re) {
                        listener.onMessage(re, Messages.get("drp.error.rollback"), OperationListener.MessageType.ERROR);
                    }
                }
            } finally {
                if (updateConnection!=null) {
                    updateConnection.close();
                }

                dbConnection.setAutoCommit(true);
            }
        }
    }

    private static void updateIQN(Connection dbConnection, List<DatabaseIQN> iqns, boolean revert, OperationListener listener) throws SQLException {

        for (DatabaseIQN iqn : iqns) {

            PreparedStatement updateIQN = null;
            String query = "UPDATE storage_server_connections SET iqn = ? WHERE iqn = ? AND iqn IS NOT NULL;";

            try {
                dbConnection.setAutoCommit(false);
                updateIQN = dbConnection.prepareStatement(query);

                if (revert) {
                    updateIQN.setString(2, iqn.destinationIQN);
                    updateIQN.setString(1, iqn.originIQN);
                } else {
                    updateIQN.setString(1, iqn.destinationIQN);
                    updateIQN.setString(2, iqn.originIQN);
                }
                updateIQN.executeUpdate();
                dbConnection.commit();
            } catch (SQLException e) {
                if (dbConnection!=null) {
                    try {
                        dbConnection.rollback();
                    } catch (SQLException re) {
                        listener.onMessage(re, Messages.get("drp.error.rollback"), OperationListener.MessageType.ERROR);
                    }
                }
            } finally {
                if (updateIQN!=null) {
                    updateIQN.close();
                }

                dbConnection.setAutoCommit(true);
            }
        }
    }
}
