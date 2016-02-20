package controllers;

import helpers.GlobalConstants;
import models.User;
import play.mvc.With;

@With(Secure.class)
@Check({GlobalConstants.ROLE_ADMIN, GlobalConstants.ROLE_TECH})
public class Application extends AuthenticatedController {
	
    public static void index() {

    	User user = getUser();
    	if (user.role!=null) {
    		switch (user.role.code) {
    		case TECNICO:
                Dashboard.index();
    			break;
    		case ADMINISTRATOR:
    			Dashboard.index();
    			break;
    		default:
    			break;
    		}
    	}

		render();
    }
}