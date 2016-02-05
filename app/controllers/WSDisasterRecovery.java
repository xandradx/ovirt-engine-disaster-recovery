package controllers;

import com.google.gson.Gson;
import drp.DisasterRecovery;
import drp.objects.OperationListener;
import models.RemoteHost;
import play.i18n.Messages;
import play.mvc.WebSocketController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jandrad on 23/01/16.
 */
public class WSDisasterRecovery extends WebSocketController {

    private static final String NONE = "none";
    private static final String ALL = "all";
    private static final String HOSTS = "hosts";
    private static final String DATACENTERS = "datacenters";

    public static void startOperation(RemoteHost.RecoveryType type) {


        if (inbound.isOpen()) {

            //WebSocketEvent event = await(inbound.nextEvent());

            DisasterRecovery disasterRecovery = new DisasterRecovery(type, new OperationListener() {
                @Override
                public void onMessage(Exception e, String message, MessageType type) {
                    if (outbound.isOpen()) {
                        outbound.send(getMessage(type, message, NONE));
                    }
                }

                @Override
                public void onFinished(String message, boolean success) {
                    if (outbound.isOpen()) {
                        if (success) {
                            outbound.send(getMessage(MessageType.SUCCESS, message, NONE));
                        } else {
                            outbound.send(getMessage(MessageType.ERROR, message, NONE));
                        }
                        disconnect();
                    }
                }

                @Override
                public void onRefreshHosts() {
                    if (outbound.isOpen()) {
                        outbound.send(getMessage(MessageType.INFO, Messages.get("Actualizando hosts"), HOSTS));
                    }
                }

                @Override
                public void onRefreshDatacenters() {
                    if (outbound.isOpen()) {
                        outbound.send(getMessage(MessageType.INFO, Messages.get("Actualizando centros de datos"), DATACENTERS));
                    }
                }
            });

            disasterRecovery.startOperation();
        }

    }

    private static String getMessage(OperationListener.MessageType type, String message, String refresh) {
        Map<String, Object> messageMap = new HashMap<String, Object>();
        messageMap.put("type", type);
        messageMap.put("message", message);
        messageMap.put("refresh", refresh);
        return new Gson().toJson(messageMap);
    }
}
