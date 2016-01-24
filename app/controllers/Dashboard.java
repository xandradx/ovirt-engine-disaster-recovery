package controllers;

import helpers.GlobalConstants;
import play.mvc.With;

@With(Secure.class)
@Check({GlobalConstants.ROLE_ADMIN, GlobalConstants.ROLE_TECH})
public class Dashboard extends AuthenticatedController {

    public static void index() {
        render();
    }

}
