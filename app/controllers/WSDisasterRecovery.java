/*
 *   Copyright 2016 ITM, S.A.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * `*   `[`http://www.apache.org/licenses/LICENSE-2.0`](http://www.apache.org/licenses/LICENSE-2.0)
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package controllers;

import com.google.gson.Gson;
import drp.DisasterRecovery;
import drp.objects.OperationListener;
import models.RemoteHost;
import play.Logger;
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

                    Logger.info("%s: %s", type, message);

                    if (outbound.isOpen()) {
                        outbound.send(getMessage(type, message, NONE));
                    }
                }

                @Override
                public void onFinished(String message, boolean success) {

                    if (success) {
                        Logger.info("SUCCESS: %s", message);
                    } else {
                        Logger.info("ERROR: %s", message);
                    }

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
