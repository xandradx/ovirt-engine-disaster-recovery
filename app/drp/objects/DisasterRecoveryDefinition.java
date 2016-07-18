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
