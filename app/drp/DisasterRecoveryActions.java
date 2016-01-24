package drp;

import drp.exceptions.ConnectionUpdateException;
import drp.exceptions.FencingException;
import drp.exceptions.HostActivateException;
import drp.exceptions.HostDeactivateException;
import drp.objects.DatabaseManager;
import drp.objects.DisasterRecoveryDefinition;
import models.DatabaseConnection;
import models.DatabaseIQN;
import models.RemoteHost;
import org.ovirt.engine.sdk.decorators.Host;
import org.ovirt.engine.sdk.entities.Action;
import org.ovirt.engine.sdk.entities.PowerManagement;
import play.Logger;

import java.sql.*;
import java.util.List;

/**
 * Created by jandrad on 23/01/16.
 */
public class DisasterRecoveryActions {

    public static void fenceHost(Host host) throws FencingException{

//        if (host == null) {
//            throw new FencingException("Invalid host");
//        }
//
//        try {
//            Logger.debug("Fencing host: %s", host.getName());
//            Action requestAction = new Action();
//            requestAction.setFenceType("manual");
//            Action resultAction = host.fence(requestAction);
//            if (!"complete".equals(resultAction.getStatus().getState())) {
//                throw new FencingException("Could not fence host " + host.getName());
//            }
//        } catch (Exception e) {
//            throw new FencingException("Could not fence host " + host.getName());
//        }
    }

    public static boolean hasPowerManagement(Host host) {
//        if (host.isSetPowerManagement()) {
//            return host.getPowerManagement().getEnabled();
//        }

        return false;
    }

    public static void enablePowerManagement(Host host) {

//        Logger.debug("Enabling power management host: %s", host.getName());
//
//        PowerManagement powerManagement = host.getPowerManagement();
//        if (powerManagement != null) {
//            powerManagement.setEnabled(true);
//            host.setPowerManagement(powerManagement);
//        }
    }

    public static void disablePowerManagement(Host host) {

//        Logger.debug("Disabling power management host: %s", host.getName());
//
//        PowerManagement powerManagement = host.getPowerManagement();
//        if (powerManagement != null) {
//            powerManagement.setEnabled(false);
//            host.setPowerManagement(powerManagement);
//        }
    }

    public static void deactivateHost(Host host) throws HostDeactivateException{
//
//        if (host == null) {
//            throw new HostDeactivateException("Invalid host");
//        }
//
//        try {
//            Logger.debug("Deactivating host: %s", host.getName());
//            Action requestAction = new Action();
//            Action resultAction = host.deactivate(requestAction);
//            if (!"complete".equals(resultAction.getStatus().getState())) {
//                throw new HostDeactivateException("Could not deactivate host " + host.getName());
//            }
//        } catch (Exception e) {
//            throw new HostDeactivateException("Could not deactivate host " + host.getName());
//        }
    }

    public static void activateHost(Host host) throws HostActivateException{

//        if (host == null) {
//            throw new HostActivateException("Invalid host");
//        }
//
//        try {
//            Logger.debug("Activating host: %s", host.getName());
//            Action requestAction = new Action();
//            Action resultAction = host.activate(requestAction);
//            if (!"complete".equals(resultAction.getStatus().getState())) {
//                throw new HostActivateException("Could not activate host " + host.getName());
//            }
//        } catch (Exception e) {
//            throw new HostActivateException("Could not activate host " + host.getName());
//        }
    }

    public static void updateConnections(DatabaseManager manager, List<DatabaseConnection> connections, List<DatabaseIQN> iqns, boolean revert) throws ConnectionUpdateException {

//        if (connections.isEmpty() && iqns.isEmpty()) {
//            throw new ConnectionUpdateException("No connections defined");
//        }
//
//        Connection connection = null;
//
//        try {
//            Class.forName("org.postgresql.Driver");
//            String databaseURL = "jdbc:postgresql://" + manager.getDbHost() + ":" + manager.getDbPort() + "/" + manager.getDbName() + "";
//            connection = DriverManager.getConnection(databaseURL, manager.getDbUser(), manager.getDbPassword());
//
//            Logger.debug("Current connections");
//            listConnections(connection);
//
//            updateConnections(connection, connections, revert);
//            updateIQN(connection, iqns, revert);
//
//            Logger.debug("Modified connections");
//            listConnections(connection);
//
//        } catch (SQLException se) {
//            Logger.error(se, "Error");
//            throw new ConnectionUpdateException("Could not connect to database");
//        } catch (ClassNotFoundException ce) {
//            throw new ConnectionUpdateException("Could not find driver");
//        } finally {
//            if (connection!=null) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    Logger.debug(e, "Could not close database connection");
//                }
//            }
//        }
    }

    private static void listConnections(Connection dbConnection) throws SQLException{

        String query = "SELECT connection, iqn FROM storage_server_connections WHERE iqn IS NOT NULL;";
        Statement statement = null;

        try {
            statement = dbConnection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Logger.debug("iqn: %s -- connection: %s", resultSet.getString(2), resultSet.getString(1));
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    private static void updateConnections(Connection dbConnection, List<DatabaseConnection> connections, boolean revert) throws SQLException {

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
                        Logger.error(re, "Could not rollback");
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

    private static void updateIQN(Connection dbConnection, List<DatabaseIQN> iqns, boolean revert) throws SQLException {

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
                        Logger.error(re, "Could not rollback");
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
