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
import dto.response.ServiceResponse;
import models.Configuration;
import org.ovirt.engine.sdk.Api;
import play.Logger;
import play.jobs.Job;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by jandrad on 30/01/16.
 */
public class ReachableJob extends Job<Boolean> {

    @Override
    public Boolean doJobWithResult() throws Exception {

        ServiceResponse serviceResponse = null;

        Configuration configuration = Configuration.generalConfiguration();
        String host = configuration.apiURL;
        if (host!=null && !host.isEmpty()) {
            try {
                String[] parts = host.split("/");
                String hostName = parts[2];
                int port = 80;
                int index = hostName.indexOf(':');
                if (index != -1) {
                    port = Integer.valueOf(hostName.substring(index+1, hostName.length()));
                    hostName = hostName.substring(0, index);
                }

                boolean reachable = isReachable(hostName, port, 1000);
                if (reachable) {
                    Api api = OvirtApi.getApi();
                    return api!=null;
                }

                return reachable;

            } catch (Exception e) {
                Logger.error(e, "Could not get manager status");
            }
        }

        return false;
    }

    private static boolean isReachable(String addr, int openPort, int timeOutMillis) {
        try {
            Socket soc = new Socket();
            soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
