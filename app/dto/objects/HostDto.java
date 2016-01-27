package dto.objects;

import models.RemoteHost;

/**
 * Created by jandrad on 26/01/16.
 */
public class HostDto {

    protected String name;
    protected String ip;
    protected String identifier;
    protected String status;
    protected RemoteHost.RecoveryType type;

    public HostDto(String name, String ip, String identifier, String status) {
        this.name = name;
        this.ip = ip;
        this.identifier = identifier;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getStatus() {
        return status;
    }

    public RemoteHost.RecoveryType getType() {
        return type;
    }

    public void setType(RemoteHost.RecoveryType type) {
        this.type = type;
    }
}
