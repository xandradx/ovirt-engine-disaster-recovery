package jobs.services;

import drp.OvirtApi;
import dto.DtoHelper;
import dto.objects.DataCenterDto;
import dto.objects.StatusDto;
import dto.response.ServiceResponse;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.DataCenter;
import play.i18n.Messages;
import play.jobs.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jandrad on 27/01/16.
 */
public class DataCentersJob extends Job<ServiceResponse> {

    @Override
    public ServiceResponse doJobWithResult() throws Exception {

        ServiceResponse serviceResponse = null;

        Api api = null;
        try {
            api = OvirtApi.getApi();
            StatusDto data = new StatusDto();

            List<DataCenter> dataCenters = api.getDataCenters().list();
            List<DataCenterDto> dataCenterDtos = new ArrayList<DataCenterDto>();
            for (DataCenter dataCenter : dataCenters) {
                data.addToStatusCount(dataCenter.getStatus().getState());
                dataCenterDtos.add(DtoHelper.getDataCenterDto(dataCenter));
            }

            data.setList(dataCenterDtos);
            serviceResponse = ServiceResponse.success(data);

        } catch (Exception e) {
            serviceResponse = ServiceResponse.error(Messages.get("ws.error.exception"));

        }

        return serviceResponse;
    }
}
