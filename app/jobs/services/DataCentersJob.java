/*
 *   Copyright 2016 ITM, S.A.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * `*   `[`http://www.apache.org/licenses/LICENSE-2.0`](http://www.apache.org/licenses/LICENSE-2.0)
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package jobs.services;

import drp.OvirtApi;
import dto.DtoHelper;
import dto.objects.DataCenterDto;
import dto.objects.StatusDto;
import dto.response.ServiceResponse;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.DataCenter;
import play.Logger;
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
            api.close();
        } catch (Exception e) {
            Logger.error(e, "Error getting datacenters");
            serviceResponse = ServiceResponse.error(Messages.get("ws.error.exception"));

        }

        return serviceResponse;
    }
}
