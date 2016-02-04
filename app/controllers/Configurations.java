package controllers;

import drp.OvirtApi;
import dto.DtoHelper;
import dto.objects.ConfigurationDto;
import dto.objects.ConnectionDto;
import dto.objects.HostDto;
import dto.objects.StatusDto;
import dto.response.ServiceResponse;
import helpers.GlobalConstants;
import jobs.services.HostsJob;
import jobs.services.ReachableJob;
import jobs.services.StorageConnectionsJob;
import models.Configuration;
import models.DatabaseConnection;
import models.DatabaseIQN;
import models.RemoteHost;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.Host;
import org.ovirt.engine.sdk.decorators.Hosts;
import org.ovirt.engine.sdk.decorators.StorageConnection;
import org.ovirt.engine.sdk.decorators.StorageConnections;
import play.Logger;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.With;
import sun.rmi.runtime.Log;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@With(Secure.class)
@Check(GlobalConstants.ROLE_ADMIN)
public class Configurations extends AuthenticatedController {

    public static void editConfiguration() {

        Configuration configuration = Configuration.generalConfiguration();
        render(configuration);
    }

    public static void save(@Valid Configuration configuration) {

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
        List<DatabaseConnection> connections = DatabaseConnection.find("active = ?", true).fetch();
        List<DatabaseIQN> iqns = DatabaseIQN.find("active = ?", true).fetch();
        render(connections, iqns);
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
        render();
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
}
