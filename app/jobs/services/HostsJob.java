package jobs.services;

import drp.OvirtApi;
import dto.DtoHelper;
import dto.objects.HostDto;
import dto.objects.StatusDto;
import dto.response.ServiceResponse;
import models.RemoteHost;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.Host;
import play.Logger;
import play.i18n.Messages;
import play.jobs.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jandrad on 27/01/16.
 */
public class HostsJob extends Job<ServiceResponse> {

    @Override
    public ServiceResponse doJobWithResult() throws Exception {

        ServiceResponse serviceResponse = null;

        Api api = null;
        try {
            api = OvirtApi.getApi();

            StatusDto data = new StatusDto();

            List<RemoteHost> remoteHosts = RemoteHost.find("active = ?", true).fetch();
            List<Host> hosts = api.getHosts().list();
            List<HostDto> hostDtos = new ArrayList<HostDto>();
            for (Host host : hosts) {
                data.addToStatusCount(host.getStatus().getState());
                HostDto dto = DtoHelper.getHostDto(host);
                dto.setType(DtoHelper.getRecoveryType(dto, remoteHosts));
                hostDtos.add(dto);
            }

            data.setList(hostDtos);
            serviceResponse = ServiceResponse.success(data);
            api.close();
        } catch (Exception e) {
            Logger.error(e, "Error getting hosts");
            serviceResponse = ServiceResponse.error(Messages.get("ws.error.exception"));

        }

        return serviceResponse;
    }
}
