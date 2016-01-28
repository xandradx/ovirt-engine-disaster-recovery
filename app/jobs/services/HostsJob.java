package jobs.services;

import drp.OvirtApi;
import dto.DtoHelper;
import dto.objects.DataCenterDto;
import dto.objects.HostDto;
import dto.response.ServiceResponse;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.DataCenter;
import org.ovirt.engine.sdk.decorators.Host;
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
            api = OvirtApi.sharedInstance().getApi();

            if (api!=null) {

                List<Host> hosts = api.getHosts().list();
                List<HostDto> hostDtos = new ArrayList<HostDto>();
                for (Host host : hosts) {
                    hostDtos.add(DtoHelper.getHostDto(host));
                }

                serviceResponse = ServiceResponse.success(hostDtos);

            } else {
                serviceResponse = ServiceResponse.error(Messages.get("ws.api.error.connection"));
            }

        } catch (Exception e) {
            serviceResponse = ServiceResponse.error(Messages.get("ws.error.exception"));

        }

        return serviceResponse;
    }
}
