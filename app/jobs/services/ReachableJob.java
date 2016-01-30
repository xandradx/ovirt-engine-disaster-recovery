package jobs.services;

import dto.response.ServiceResponse;
import models.Configuration;
import play.Logger;
import play.jobs.Job;

import java.io.IOException;
import java.net.InetAddress;
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

                return isReachable(hostName, port, 1000);

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
