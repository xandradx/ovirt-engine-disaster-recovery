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

import com.jcabi.ssh.SSH;
import com.jcabi.ssh.SSHByPassword;
import com.jcabi.ssh.Shell;
import drp.OvirtApi;
import dto.response.ServiceResponse;
import models.Configuration;
import org.ovirt.engine.sdk.Api;
import play.Logger;
import play.i18n.Messages;
import play.jobs.Job;

import java.io.File;
import java.util.Scanner;

/**
 * Created by jandrad on 31/01/16.
 */
public class ManagerJob extends Job {

    @Override
    public ServiceResponse doJobWithResult() throws Exception {

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

                String user = configuration.managerUser;
                String ip = configuration.managerIp;
                String bin = configuration.managerBinLocation;
                String operation = configuration.managerCommand;

                Shell shell;
                if (configuration.managerKey!=null && configuration.managerKey.exists()) {
                    File keyFile = configuration.managerKey.getFile();
                    String keyContent = new Scanner(keyFile).useDelimiter("\\Z").next();
                    shell = new SSH(ip, 22, user, keyContent);
                } else {
                    shell = new SSHByPassword(ip, 22, user, configuration.managerPassword);
                }

                try {
                    String stdout = new Shell.Plain(new Shell.Safe(shell)).exec(bin + " " + operation);
                    Logger.debug("Output: %s", stdout);

                } catch (IllegalArgumentException e) {
                    return ServiceResponse.error(Messages.get("manager.startupfailed"));
                }

                long startTime = System.currentTimeMillis();
                Api api = null;
                do {
                    try {
                        api = OvirtApi.getApi();
                    } catch (Exception e) {
                        Logger.debug("Api not ready yet");
                    }

                } while (api == null && (System.currentTimeMillis() - startTime) < 60000);

                if (api!=null) {
                    api.close();
                    return ServiceResponse.success("");
                } else {
                    return ServiceResponse.error(Messages.get("manager.startupfailed"));
                }

            } catch (Exception e) {
                Logger.error(e, "Could not get manager status");
            }
        }

        return ServiceResponse.error(Messages.get("manager.startupfailed"));
    }
}
