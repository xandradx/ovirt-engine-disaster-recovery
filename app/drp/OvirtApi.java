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
package drp;

import drp.exceptions.InvalidConfigurationException;
import models.Configuration;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.ApiBuilder;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirt.engine.sdk.exceptions.UnsecuredConnectionAttemptError;
import play.i18n.Messages;

import java.io.File;
import java.io.IOException;

/**
 * Created by jandrad on 24/01/16.
 */
public class OvirtApi {

    public static Api getApi() throws UnsecuredConnectionAttemptError, IOException, ServerException, InvalidConfigurationException {

        Configuration configuration = Configuration.generalConfiguration();
        if (configuration.apiURL == null || configuration.apiPassword == null || configuration.apiUser == null) {
            throw new InvalidConfigurationException(Messages.get("drp.invalidconfiguration"));
        }

        ApiBuilder builder = new ApiBuilder().url(configuration.apiURL).password(configuration.apiPassword).user(configuration.apiUser);

        if (configuration.validateCertificate) {
            if (configuration.trustStore!=null && configuration.trustStore.exists()) {
                File cert = configuration.trustStore.getFile();
                builder = builder.keyStorePath(cert.getAbsolutePath());

                if (configuration.trustStorePassword!=null && !configuration.trustStorePassword.isEmpty()) {
                    builder = builder.keyStorePassword(configuration.trustStorePassword);
                }
            } else {
                throw new InvalidConfigurationException(Messages.get("drp.invalidconfiguration"));
            }
        } else {

            builder = builder.noHostVerification(true);
        }

        return builder.build();
    }
}
