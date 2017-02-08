package controllers;

import dto.response.ServiceResponse;
import helpers.GlobalConstants;
import jobs.services.HostsJob;
import jobs.services.StorageConnectionsJob;
import models.Configuration;
import models.DatabaseConnection;
import models.DatabaseIQN;
import models.RemoteHost;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
@Check(GlobalConstants.ROLE_ADMIN)
public class Configurations extends AuthenticatedController {

    public static void editConfiguration() {

        Configuration configuration = Configuration.generalConfiguration();
        render(configuration);
    }

    public static void save(@Valid Configuration configuration) {

        if (configuration.startVMManager) {
            validation.required("configuration.managerUser", configuration.managerUser);
            validation.required("configuration.managerBinLocation", configuration.managerBinLocation);
            validation.required("configuration.managerKeyLocation", configuration.managerKeyLocation);
            validation.required("configuration.managerCommand", configuration.managerCommand);
        }

        if (validation.hasErrors()) {
            params.flash();
            flash.error(Messages.get("form.error"));
            validation.keep();
        } else {
            flash.success(Messages.get("form.success"));
            Configuration generalConfiguration = Configuration.generalConfiguration();
            generalConfiguration.applyConfiguration(configuration);
            generalConfiguration.save();
        }

        editConfiguration();
    }

    public static void editStorageConnections() {
        Configuration configuration = Configuration.generalConfiguration();
        List<DatabaseConnection> connections = DatabaseConnection.find("active = :a").bind("a", true).fetch();
        List<DatabaseIQN> iqns = DatabaseIQN.find("active = :a").bind("a", true).fetch();
        render(connections, iqns, configuration);
    }

    public static void saveConnections(@Valid List<DatabaseConnection> connections, @Valid List<DatabaseIQN> iqns) {

        if (validation.hasErrors()) {
            params.flash();
            flash.error(Messages.get("form.error"));
            validation.keep();
        } else {

            DatabaseIQN.deleteAll();
            for (DatabaseIQN iqn : iqns) {
                iqn.save();
            }

            DatabaseConnection.deleteAll();
            for (DatabaseConnection connection : connections) {
                connection.save();
            }

            flash.success(Messages.get("form.success"));
        }

        editStorageConnections();
    }

    public static void editHosts() {
        Configuration configuration = Configuration.generalConfiguration();
        render(configuration);
    }

    public static void getHosts() {
        F.Promise<ServiceResponse> apiHosts = new HostsJob().now();
        ServiceResponse connectionsService = await(apiHosts);
        renderJSON(connectionsService);
    }

    public static void saveHosts(@Valid List<RemoteHost> hosts) {

        if (validation.hasErrors()) {
            params.flash();
            flash.error(Messages.get("form.error"));
            validation.keep();
        } else {

            RemoteHost.deleteAll();
            for (RemoteHost host : hosts) {
                if (host.type != RemoteHost.RecoveryType.NONE) {
                    host.save();
                }
            }

            flash.success(Messages.get("form.success"));

        }

        editHosts();
    }

    public static void getStorageConnections() {
        F.Promise<ServiceResponse> storageResponse = new StorageConnectionsJob().now();
        ServiceResponse serviceResponse = await(storageResponse);
        renderJSON(serviceResponse);
    }

    public static void downloadTrustStore() {
        Configuration configuration = Configuration.generalConfiguration();
        if (configuration.trustStore!=null && configuration.trustStore.getFile().exists()) {
            response.setContentTypeIfNotSet(configuration.trustStore.type());
            renderBinary(configuration.trustStore.getFile());
        }

        notFound();
    }
}
