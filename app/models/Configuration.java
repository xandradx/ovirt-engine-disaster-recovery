package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.Crypto;

import javax.persistence.*;


/**
 * Created by jandrad on 26/09/15.
 */
@Entity
public class Configuration extends Model {


    @Required
    @MaxSize(200)
    public String apiURL;

    @Required
    @MaxSize(100)
    public String apiUser;

    @Required
    @MaxSize(100)
    public String apiPassword;

    @Required
    public Blob trustStore;

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
        apiURL = configuration.apiURL;
        apiUser = configuration.apiUser;
        apiPassword = configuration.apiPassword;
        trustStore = configuration.trustStore;
    }

    @PreUpdate
    @PrePersist
    public void encryptPassword() {
        if (apiPassword!=null) {
            apiPassword = Crypto.encryptAES(apiPassword);
        }
    }

    @PostLoad
    public void decryptPassword() {
        if (apiPassword!=null) {
            apiPassword = Crypto.decryptAES(apiPassword);
        }
    }
}
