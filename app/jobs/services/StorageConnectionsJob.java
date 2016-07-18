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
import dto.objects.ConnectionDto;
import dto.response.ServiceResponse;
import org.ovirt.engine.sdk.Api;
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
