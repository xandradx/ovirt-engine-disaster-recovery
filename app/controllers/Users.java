package controllers;

import helpers.GlobalConstants;

import java.util.List;

import models.Configuration;
import models.User;
import models.UserRole;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.With;

@With(Secure.class)
@Check(GlobalConstants.ROLE_ADMIN)
public class Users extends AuthenticatedController {

    public static void index() {
    	User connectedUser = getUser();
    	List<User> users = User.find("id != ?", connectedUser.id).fetch();
        render(users);
    }
    
    public static void create() {
    	List<UserRole> roles = UserRole.findAll();
        renderTemplate("Users/editUser.html", roles);
    }
    
    public static void edit(Long id) {
    	
    	if (id!=null) {
    		User user = User.findById(id);
    		if (user!=null) {
    			List<UserRole> roles = UserRole.findAll();
                renderTemplate("Users/editUser.html",roles, user);
    		}
    	}
    	
    	index();
    }
    
    public static void saveNewUser(@Valid User user) {
    	
    	if (validation.hasErrors()) {
    		params.flash();
    		flash.error(Messages.get("form.error"));
    		validation.keep();
    		create();
    	} else {
    		flash.success(Messages.get("form.success"));
    		user.password = user.username;
            user.needsPasswordReset = true;
    		user.save();
    		index();
    	}
    }
    
    public static void saveUser(@Valid User user) {
    	
    	if (validation.hasErrors()) {
    		params.flash();
    		flash.error(Messages.get("form.error"));
    		validation.keep();
    		edit(user.id);
    	} else {
    		flash.success(Messages.get("form.success"));
    		user.save();
    		index();
    	}
    }

    public static void resetPassword(Long id) {

        if (id!=null) {
            User user = User.findById(id);
            if (user!=null) {
                user.password = user.username;
                user.needsPasswordReset = true;
                user.save();
                flash.success(Messages.get("form.success"));
            }
        }

        index();
    }

    public static void delete(Long id) {
        if (id!=null) {
            User user = User.findById(id);
            if (user!=null) {
                user.delete();
                flash.success(Messages.get("form.success"));
            }
        }

        index();
    }
}
