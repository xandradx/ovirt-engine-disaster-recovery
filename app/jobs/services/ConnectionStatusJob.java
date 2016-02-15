package jobs.services;

import dto.objects.ConnectionDto;
import dto.response.ServiceResponse;
import models.DatabaseConnection;
import models.DatabaseIQN;
import org.h2.engine.Database;
import org.omg.CORBA.UNKNOWN;
import play.Logger;
import play.jobs.Job;
import play.libs.F;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jandrad on 2/02/16.
 */


public class ConnectionStatusJob extends Job<ConnectionStatusJob.ConnectionStatus> {

    public enum ConnectionStatus {
        UNKNOWN,
        PRODUCTION,
        CONTINGENCY
    }

    @Override
    public ConnectionStatus doJobWithResult() throws Exception {

        F.Promise<ServiceResponse> storageResponse = new StorageConnectionsJob().now();
        ServiceResponse serviceResponse = storageResponse.get();
        if (serviceResponse.isSuccess()) {

            int contingency = 0;
            int production = 0;
            int unknown = 0;

            //Get connections
            List<DatabaseConnection> connections = DatabaseConnection.find("active = ?", true).fetch();
            List<String> originIPs = new ArrayList<String>();
            List<String> destinationIPs = new ArrayList<String>();
            for (DatabaseConnection connection : connections) {
                originIPs.add(connection.originConnection);
                destinationIPs.add(connection.destinationConnection);
            }

            //Get iqns
            List<DatabaseIQN> iqns = DatabaseIQN.find("active = ?", true).fetch();
            List<String> originIqns = new ArrayList<String>();
            List<String> destinationIqns = new ArrayList<String>();
            for (DatabaseIQN iqn : iqns) {
                originIqns.add(iqn.originIQN);
                destinationIqns.add(iqn.destinationIQN);
            }


            for (ConnectionDto dto : (List<ConnectionDto>)serviceResponse.getData()) {

                if (originIqns.contains(dto.getIqn()) && originIPs.contains(dto.getIpAddress())) {
                    production++;
                } else if (destinationIqns.contains(dto.getIqn()) && destinationIPs.contains(dto.getIpAddress())) {
                    contingency++;
                } else {
                    unknown++;
                }
            }

            if (contingency > 0 && production==0 && unknown == 0) {
                return ConnectionStatus.CONTINGENCY;
            } else if (production > 0 && contingency==0 && unknown == 0) {
                return ConnectionStatus.PRODUCTION;
            }

        }

        return ConnectionStatus.UNKNOWN;
    }
}
