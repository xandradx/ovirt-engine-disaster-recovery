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

    private static OvirtApi instance;

    private Api api;

    public static OvirtApi sharedInstance() {
        if (instance == null) {
            instance = new OvirtApi();
        }

        return instance;
    }

    public Api getApi() {

        if (api == null) {
            try {
                Logger.debug("Connecting to API..");
                File cert = Play.getFile(TRUSTSTORE_PATH);
                api = new Api(API_URL, USER, PASSWORD, cert.getAbsolutePath());
                api.setSessionid(SESSION_ID);
            } catch (Exception e) {
                Logger.error(e, "Could not connect to API");
            }
        }

        return api;
    }
}
