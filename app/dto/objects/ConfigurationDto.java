package dto.objects;

import java.util.List;

/**
 * Created by jandrad on 26/01/16.
 */
public class ConfigurationDto {

    protected List<HostDto> hosts;
    protected List<ConnectionDto> connections;

    public ConfigurationDto(List<HostDto> hosts, List<ConnectionDto> connections) {
        this.hosts = hosts;
        this.connections = connections;
    }

    public List<HostDto> getHosts() {
        return hosts;
    }

    public List<ConnectionDto> getConnections() {
        return connections;
    }
}
