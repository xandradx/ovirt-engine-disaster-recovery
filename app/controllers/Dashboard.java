package controllers;

import drp.DisasterRecovery;
import drp.OvirtApi;
import dto.DtoHelper;
import dto.objects.DataCenterDto;
import dto.response.ServiceResponse;
import helpers.GlobalConstants;
import jobs.services.DataCentersJob;
import jobs.services.HostsJob;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.DataCenter;
import play.Logger;
import play.Play;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.With;
import sun.rmi.runtime.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@With(Secure.class)
@Check({GlobalConstants.ROLE_ADMIN, GlobalConstants.ROLE_TECH})
public class Dashboard extends AuthenticatedController {

    public static void index() {
        render();
    }

    public static void getDataCenters() {
        F.Promise<ServiceResponse> dataCentersResponse = new DataCentersJob().now();
        ServiceResponse serviceResponse = await(dataCentersResponse);
        renderJSON(serviceResponse);
    }

    public static void getHosts() {
        F.Promise<ServiceResponse> dataCentersResponse = new HostsJob().now();
        ServiceResponse serviceResponse = await(dataCentersResponse);
        renderJSON(serviceResponse);
    }

}
