package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * Created by jandrad on 26/09/15.
 */
@Entity
public class Configuration extends Model {


    private Configuration() {

    }

    public static Configuration generalConfiguration() {

        Configuration configuration = Configuration.find("").first();
        if (configuration == null) {
            configuration = new Configuration();
            configuration.save();
        }

        return configuration;
    }

    public void applyConfiguration(Configuration configuration) {

    }
}
