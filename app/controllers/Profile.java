package controllers;

import models.User;
import play.data.validation.Equals;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.With;

/**
 * Created by jandrad on 21/09/15.
 */

@With(Secure.class)
public class Profile extends AuthenticatedController {

    public static void index() {
        User user = getUser();
        render(user);
    }

    public static void save(@Required @MaxSize(45) String firstName, @Required @MaxSize(45) String lastName,
                            @MaxSize(45) @MinSize(6) @Equals(value = "passwordConfirm", message = "validation.equals.password") String password,
                            @MaxSize(45) @MinSize(6) @Equals(value = "password", message = "validation.equals.password") String passwordConfirm) {

        if (validation.hasErrors()) {
            params.flash();
            flash.error(Messages.get("form.error"));
            validation.keep();
            index();
        } else {

            User user = getUser();
            user.firstName = firstName;
            user.lastName = lastName;
            if (password!=null && !password.isEmpty()) {
                user.password = password;
            }
            user.needsPasswordReset = false;
            user.save();

            flash.success(Messages.get("form.success"));
            index();
        }
    }

    public static void passwordChange() {
        render();
    }

    public static void changePassword( @Required @MaxSize(45) @MinSize(6) @Equals(value = "passwordConfirm", message = "validation.equals.password") String password,
                            @Required @MaxSize(45) @MinSize(6) @Equals(value = "password", message = "validation.equals.password") String passwordConfirm) {

        if (validation.hasErrors()) {
            params.flash();
            flash.error(Messages.get("form.error"));
            validation.keep();
            index();
        } else {

            User user = getUser();
            user.password = password;
            user.needsPasswordReset = false;
            user.save();
            flash.success(Messages.get("form.success"));
            Application.index();
        }
    }
}
