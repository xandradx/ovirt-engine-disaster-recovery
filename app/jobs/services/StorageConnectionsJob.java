package jobs.services;

import drp.OvirtApi;
import dto.DtoHelper;
import dto.objects.ConnectionDto;
import dto.objects.HostDto;
import dto.objects.StatusDto;
import dto.response.ServiceResponse;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.Host;
import org.ovirt.engine.sdk.decorators.StorageConnection;
import play.Logger;
import play.i18n.Messages;
import play.jobs.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jandrad on 27/01/16.
 */
public class StorageConnectionsJob extends Job<ServiceResponse> {

    @Override
    public ServiceResponse doJobWithResult() throws Exception {

        ServiceResponse serviceResponse = null;

        Api api = null;
        try {
            api = OvirtApi.getApi();

            List<StorageConnection> connections = api.getStorageConnections().list();
            List<ConnectionDto> connectionDtos = new ArrayList<ConnectionDto>();
            for (StorageConnection storageConnection : connections) {
                if ("iscsi".equalsIgnoreCase(storageConnection.getType())) {
                    ConnectionDto dto = DtoHelper.getConnectionDto(storageConnection);
                    connectionDtos.add(dto);
                }
            }

            serviceResponse = ServiceResponse.success(connectionDtos);
            api.close();
        } catch (Exception e) {
            Logger.error(e, "Error getting storage connections");
            serviceResponse = ServiceResponse.error(Messages.get("ws.error.exception"));

        }

        return serviceResponse;
    }
}
