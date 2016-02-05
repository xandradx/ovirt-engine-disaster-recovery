package controllers;

import models.User;
import models.UserRole;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.util.Date;

@With(Secure.class)
public class AuthenticatedController extends Controller {

	@Before()
	public static void getUserInfo() {
	
		User user = User.find("username = ?", Security.connected()).first();
		if (user!=null) {

            user.lastActivity = new Date();
            user.save();

            setUser(user);
            if (!request.action.equals("Profile.passwordChange") && !request.action.equals("Profile.changePassword") && user.needsPasswordReset) {
                Profile.passwordChange();
            }
		}
	}
	
	protected static void setUser(User user) {
        boolean isAdmin = (user.role!=null && user.role.code == UserRole.RoleCode.ADMINISTRATOR);
        boolean isTecnico = (user.role!=null && user.role.code == UserRole.RoleCode.TECNICO);

        renderArgs.put("isAdmin", isAdmin);
        renderArgs.put("isTecnico", isTecnico);
		renderArgs.put("connectedUser", user);
	}
	
	protected static User getUser() {
		return (User)renderArgs.get("connectedUser");
	}
}
