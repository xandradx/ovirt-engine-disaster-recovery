package controllers;

import helpers.GlobalConstants;
import models.Configuration;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.With;

@With(Secure.class)
@Check(GlobalConstants.ROLE_ADMIN)
public class Configurations extends AuthenticatedController {

    public static void editConfiguration() {

        Configuration configuration = Configuration.generalConfiguration();
        render(configuration);
    }

    public static void save(@Valid Configuration configuration) {

        if (validation.hasErrors()) {
            params.flash();
            flash.error(Messages.get("form.error"));
            validation.keep();
        } else {
            flash.success(Messages.get("form.success"));

            Configuration generalConfiguration = Configuration.generalConfiguration();
            generalConfiguration.applyConfiguration(configuration);
            generalConfiguration.save();
        }

        editConfiguration();
    }
}
