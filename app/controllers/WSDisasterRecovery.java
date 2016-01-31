package controllers;

import com.google.gson.Gson;
import drp.DisasterRecovery;
import drp.objects.OperationListener;
import models.RemoteHost;
import play.mvc.WebSocketController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jandrad on 23/01/16.
 */
public class WSDisasterRecovery extends WebSocketController {

    public static void startOperation(RemoteHost.RecoveryType type) {


        if (inbound.isOpen()) {

            //WebSocketEvent event = await(inbound.nextEvent());

            DisasterRecovery disasterRecovery = new DisasterRecovery(type, new OperationListener() {
                @Override
                public void onMessage(String message) {
                    if (outbound.isOpen()) {
                        outbound.send(getMessage(false, message, false));
                    }
                }

                @Override
                public void onFinished(String message, boolean success) {
                    if (outbound.isOpen()) {
                        outbound.send(getMessage(!success, message, true));
                        disconnect();
                    }
                }

                @Override
                public void onError(Exception e, String error) {
                    if (outbound.isOpen()) {
                        outbound.send(getMessage(true, error, false));
                    }
                }
            });

            disasterRecovery.startOperation();
        }

    }

    private static String getMessage(boolean error, String message, boolean refresh) {
        Map<String, Object> messageMap = new HashMap<String, Object>();
        messageMap.put("type", error ? "error" : "message");
        messageMap.put("message", message);
        messageMap.put("refresh", refresh);
        return new Gson().toJson(messageMap);
    }
}
