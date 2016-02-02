package drp;

import drp.exceptions.InvalidConfigurationException;
import models.Configuration;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirt.engine.sdk.exceptions.UnsecuredConnectionAttemptError;
import play.Logger;
import play.Play;
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

        //File cert = configuration.trustStore.getFile();
        Api api = new Api(configuration.apiURL, configuration.apiUser, configuration.apiPassword, true);
        return api;
    }
}
