package controllers;

import models.User;
import play.i18n.Messages;

import java.util.Date;

public class Security extends Secure.Security {

	static String authenticate(String username, String password) {
		
        User user = User.find("username = ?", username).first();

        if (user == null || !user.password.equals(password)) {
        	return Messages.get("secure.invaliduser");
        } if (user!=null && !user.active) {
        	return Messages.get("secure.inactiveuser");
        }

        user.lastAccess = new Date();
        user.lastActivity = new Date();
        user.save();
        
        return "true";
    }
	
	static boolean check(String profile) {
		
		User user = (User)renderArgs.get("connectedUser");
		if (user == null) {
			user = User.find("username", connected()).first();
		}
		
        return (user.role!=null && user.role.code!=null && user.role.code.toString().equals(profile));
    }

    static void onDisconnect() {

    }

}
