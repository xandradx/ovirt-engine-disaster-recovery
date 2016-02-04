package jobs.services;

import drp.OvirtApi;
import dto.DtoHelper;
import dto.objects.HostDto;
import dto.objects.StatusDto;
import dto.response.ServiceResponse;
import models.RemoteHost;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.Host;
import play.i18n.Messages;
import play.jobs.Job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jandrad on 3/02/16.
 */
public class SegmentedHostsJob extends Job<ServiceResponse> {

    @Override
    public ServiceResponse doJobWithResult() throws Exception {

        ServiceResponse serviceResponse = null;

        Api api = null;
        try {
            api = OvirtApi.getApi();

            StatusDto dataProduction = new StatusDto();
            StatusDto dataContingency = new StatusDto();

            List<RemoteHost> remoteHosts = RemoteHost.find("active = ?", true).fetch();
            List<Host> hosts = api.getHosts().list();

            List<HostDto> productionHosts = new ArrayList<HostDto>();
            List<HostDto> contingengyHosts = new ArrayList<HostDto>();

            for (Host host : hosts) {
                HostDto dto = DtoHelper.getHostDto(host);
                dto.setType(DtoHelper.getRecoveryType(dto, remoteHosts));

                if (dto.getType() == RemoteHost.RecoveryType.FAILOVER) {
                    contingengyHosts.add(dto);
                    dataContingency.addToStatusCount(host.getStatus().getState());
                } else {
                    productionHosts.add(dto);
                    dataProduction.addToStatusCount(host.getStatus().getState());
                }
            }

            dataProduction.setList(productionHosts);
            dataContingency.setList(contingengyHosts);

            Map<String, Object> responseMap = new HashMap<String, Object>();
            responseMap.put("production", dataProduction);
            responseMap.put("contingency", dataContingency);

            serviceResponse = ServiceResponse.success(responseMap);
            api.close();
        } catch (Exception e) {
            serviceResponse = ServiceResponse.error(Messages.get("ws.error.exception"));

        }

        return serviceResponse;
    }

}
