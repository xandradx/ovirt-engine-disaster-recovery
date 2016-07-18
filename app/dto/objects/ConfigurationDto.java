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
