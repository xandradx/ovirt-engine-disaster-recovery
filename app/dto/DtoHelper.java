package dto;

import dto.objects.ConnectionDto;
import dto.objects.DataCenterDto;
import dto.objects.HostDto;
import models.RemoteHost;
import org.ovirt.engine.sdk.decorators.DataCenter;
import org.ovirt.engine.sdk.decorators.Host;
import org.ovirt.engine.sdk.decorators.StorageConnection;
import play.Logger;
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

    public static RemoteHost.RecoveryType getRecoveryType(Host host, List<RemoteHost> remoteHosts) {
        for (RemoteHost remoteHost : remoteHosts) {
            if (remoteHost.hostName.equalsIgnoreCase(host.getName())) {
                return remoteHost.type;
            }
        }

        return RemoteHost.RecoveryType.NONE;
    }
}
