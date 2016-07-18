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
