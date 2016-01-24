package controllers;

import com.ning.http.client.websocket.WebSocket;
import drp.DisasterRecovery;
import drp.objects.OperationListener;
import jobs.DRPOperationJob;
import models.RemoteHost;
import play.Logger;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Http.WebSocketEvent;
import play.mvc.WebSocketController;

/**
 * Created by jandrad on 23/01/16.
 */
public class WSDisasterRecovery extends WebSocketController {

    public static void startOperation() {


        if (inbound.isOpen()) {

            //WebSocketEvent event = await(inbound.nextEvent());

            DisasterRecovery disasterRecovery = new DisasterRecovery(RemoteHost.RecoveryType.FAILOVER, new OperationListener() {
                @Override
                public void onMessage(String message) {
                    outbound.send(message);
                }

                @Override
                public void onFinished(String message) {
                    outbound.send(message);
                    disconnect();
                }

                @Override
                public void onError(Exception e, String error) {
                    outbound.send(error);
                }
            });

            disasterRecovery.startOperation();
        }

    }
}
