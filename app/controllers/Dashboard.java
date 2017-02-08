package controllers;

import dto.response.ServiceResponse;
import helpers.GlobalConstants;
import jobs.services.*;
import models.Configuration;
import play.libs.F;
import play.mvc.With;

@With(Secure.class)
@Check({GlobalConstants.ROLE_ADMIN, GlobalConstants.ROLE_TECH})
public class Dashboard extends AuthenticatedController {

    public static void index() {
        Configuration configuration = Configuration.generalConfiguration();
        render(configuration);
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

    public static void getSegmentedHosts() {
        F.Promise<ServiceResponse> dataCentersResponse = new SegmentedHostsJob().now();
        ServiceResponse serviceResponse = await(dataCentersResponse);
        renderJSON(serviceResponse);
    }

    public static void checkManagerStatus() {

        F.Promise<Boolean> apiReachable = new ReachableJob().now();
        Boolean reachable = await(apiReachable);

        ServiceResponse serviceResponse = ServiceResponse.success(reachable);
        renderJSON(serviceResponse);
    }

    public static void turnOnApi() {

        F.Promise<Boolean> apiReachable = new ManagerJob().now();
        Boolean reachable = await(apiReachable);

        ServiceResponse serviceResponse = ServiceResponse.success(reachable);
        renderJSON(serviceResponse);
    }

    public static void checkConnectionStatus() {

        F.Promise<ConnectionStatusJob.ConnectionStatus> apiStatus = new ConnectionStatusJob().now();
        ConnectionStatusJob.ConnectionStatus status = await(apiStatus);

        ServiceResponse serviceResponse = ServiceResponse.success(status);
        renderJSON(serviceResponse);
    }

}
