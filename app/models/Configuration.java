package models;

import play.Logger;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.Crypto;
import sun.rmi.runtime.Log;

import javax.persistence.*;


/**
 * Created by jandrad on 26/09/15.
 */
@Entity
public class Configuration extends Model {


    @Required
    @MaxSize(200)
    @Column(length = 200)
    public String apiURL;

    @Required
    @MaxSize(100)
    @Column(length = 100)
    public String apiUser;

    @Required
    @MaxSize(100)
    @Column(length = 100)
    public String apiPassword;

    public boolean validateCertificate;

    public Blob trustStore;

    @Required
    @MaxSize(50)
    @Column(length = 50)
    public String managerIp;

    @Required
    @MaxSize(100)
    @Column(length = 100)
    public String managerUser;

    @Required
    @MaxSize(100)
    @Column(length = 100)
    public String managerKeyLocation;

    @Required
    @MaxSize(100)
    @Column(length = 100)
    public String managerBinLocation;

    @Required
    @MaxSize(100)
    @Column(length = 100)
    public String managerCommand;

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
        validateCertificate = configuration.validateCertificate;
        managerIp = configuration.managerIp;
        managerUser = configuration.managerUser;
        managerKeyLocation = configuration.managerKeyLocation;
        managerBinLocation = configuration.managerBinLocation;
        managerCommand = configuration.managerCommand;


        if (configuration.trustStore!=null && configuration.trustStore.exists()) {
            if (trustStore!=null && trustStore.exists()) {
                trustStore.getFile().delete();
            }

            trustStore = configuration.trustStore;
        }
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
