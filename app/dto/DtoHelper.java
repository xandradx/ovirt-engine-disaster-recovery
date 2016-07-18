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
package dto;

import dto.objects.ConnectionDto;
import dto.objects.DataCenterDto;
import dto.objects.HostDto;
import models.RemoteHost;
import org.ovirt.engine.sdk.decorators.DataCenter;
import org.ovirt.engine.sdk.decorators.Host;
import org.ovirt.engine.sdk.decorators.StorageConnection;
import play.i18n.Messages;

import java.util.List;

/**
 * Created by jandrad on 26/01/16.
 */
public class DtoHelper {

    public static HostDto getHostDto(Host host) {
        return new HostDto(host.getName(), host.getAddress(), host.getDescription(), Messages.get(host.getStatus().getState()));
    }

    public static ConnectionDto getConnectionDto(StorageConnection connection) {
        return new ConnectionDto(connection.getAddress(), connection.getTarget());
    }

    public static DataCenterDto getDataCenterDto(DataCenter dataCenter) {
        return new DataCenterDto(dataCenter.getName(), Messages.get(dataCenter.getStatus().getState()));
    }

    public static RemoteHost.RecoveryType getRecoveryType(HostDto dto, List<RemoteHost> remoteHosts) {
        for (RemoteHost remoteHost : remoteHosts) {
            if (remoteHost.hostName.equalsIgnoreCase(dto.getName())) {
                return remoteHost.type;
            }
        }

        return RemoteHost.RecoveryType.NONE;
    }
}
