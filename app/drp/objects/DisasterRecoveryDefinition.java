package drp.objects;

import org.ovirt.engine.sdk.decorators.Host;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jandrad on 23/01/16.
 */
public class DisasterRecoveryDefinition {

    protected List<Host> localHosts = new ArrayList<Host>();
    protected List<Host> remoteHosts = new ArrayList<Host>();

    public DisasterRecoveryDefinition() {

    }

    public void addLocalHost(Host host) {
        this.localHosts.add(host);
    }

    public void addRemoteHost(Host host) {
        this.remoteHosts.add(host);
    }

    public List<Host> getLocalHosts() {
        return localHosts;
    }

    public List<Host> getRemoteHosts() {
        return remoteHosts;
    }
}
