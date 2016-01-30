package drp;

import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirt.engine.sdk.exceptions.UnsecuredConnectionAttemptError;
import play.Logger;
import play.Play;

import java.io.File;
import java.io.IOException;

/**
 * Created by jandrad on 24/01/16.
 */
public class OvirtApi {

    public static String API_URL = "https://nrhevm.itmlabs.local:443/api";
    public static String USER = "admin@internal";
    public static String PASSWORD = "pruebas";
    public static String TRUSTSTORE_PATH = "/conf/server.truststore";
    public static String SESSION_ID = "test123123";

    public static Api getApi() throws UnsecuredConnectionAttemptError, IOException, ServerException {

        Api api = null;
        File cert = Play.getFile(TRUSTSTORE_PATH);
        api = new Api(API_URL, USER, PASSWORD, cert.getAbsolutePath());
        api.setSessionid(SESSION_ID);

        return api;
    }
}
