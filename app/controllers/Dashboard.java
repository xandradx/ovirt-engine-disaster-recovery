package controllers;

import drp.DisasterRecovery;
import drp.OvirtApi;
import helpers.GlobalConstants;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.DataCenter;
import play.Logger;
import play.Play;
import play.mvc.With;
import sun.rmi.runtime.Log;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@With(Secure.class)
@Check({GlobalConstants.ROLE_ADMIN, GlobalConstants.ROLE_TECH})
public class Dashboard extends AuthenticatedController {

    public static void index() {
        render();
    }

    public static void listDashboardData() {

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("success", false);

        Api api = null;

        try {
            File cert = Play.getFile(DisasterRecovery.TRUSTSTORE_PATH);
            api = OvirtApi.sharedInstance().getApi();
            Logger.debug("Getting datacenters...");
            List<DataCenter> dataCenters = api.getDataCenters().list();
            for (DataCenter dataCenter : dataCenters) {
                Logger.debug("%s -- %s", dataCenter.getName(), dataCenter.getStatus());
            }


        } catch (Exception e) {
            resultMap.put("message", e.getMessage());
            Logger.error("Could not perform operation");

        }

        renderJSON(resultMap);

    }

}
