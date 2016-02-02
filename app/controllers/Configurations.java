package controllers;

import drp.OvirtApi;
import dto.DtoHelper;
import dto.objects.ConfigurationDto;
import dto.objects.ConnectionDto;
import dto.objects.HostDto;
import dto.response.ServiceResponse;
import helpers.GlobalConstants;
import models.Configuration;
import models.RemoteHost;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.Host;
import org.ovirt.engine.sdk.decorators.Hosts;
import org.ovirt.engine.sdk.decorators.StorageConnection;
import org.ovirt.engine.sdk.decorators.StorageConnections;
import play.Logger;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.With;

import java.util.ArrayList;
import java.util.List;

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

    public static void editHosts() {
        render();
    }

    public static void editStorageConnections() {
        render();
    }

    private static ServiceResponse getConfiguration() {
        ServiceResponse serviceResponse;

        try {
            Api api = OvirtApi.getApi();
            if (api!=null) {

                List<RemoteHost> remoteHosts = RemoteHost.find("active = ?", true).fetch();

                Hosts hosts = api.getHosts();
                List<HostDto> hostsDto = new ArrayList<HostDto>();
                for (Host host : hosts.list()) {
                    HostDto dto = DtoHelper.getHostDto(host);
                    dto.setType(DtoHelper.getRecoveryType(host, remoteHosts));
                    hostsDto.add(dto);
                }

                List<models.StorageConnection> dbConnections = models.StorageConnection.find("active = ?", true).fetch();
                StorageConnections connections = api.getStorageConnections();
                List<ConnectionDto> connectionDtos = new ArrayList<ConnectionDto>();
                for (StorageConnection connection : connections.list()) {
                    if (connection.getType().equalsIgnoreCase("iscsi")) {
                        ConnectionDto dto =  DtoHelper.getConnectionDto(connection);
                        models.StorageConnection dbConnection = DtoHelper.getDestinationInformation(connection, dbConnections);
                        if (dbConnection!=null) {
                            dto.setDestinationAddress(dbConnection.destinationIp);
                            dto.setDestinationIqn(dbConnection.originIqn);
                        }

                        connectionDtos.add(dto);

                    }
                }

                ConfigurationDto configuration = new ConfigurationDto(hostsDto, connectionDtos);
                serviceResponse = ServiceResponse.success(configuration);

            } else {
                serviceResponse = ServiceResponse.error(Messages.get("ws.api.error.connection"));
            }
        } catch (Exception e) {
            Logger.error(e, "Error");
            serviceResponse = ServiceResponse.error(Messages.get("ws.error.exception"));
        }

        return serviceResponse;
    }

    public static void getHosts() {
        renderJSON(getConfiguration());
    }
}
