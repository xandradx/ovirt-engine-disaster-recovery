package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by jandrad on 23/01/16.
 */
@Entity
public class DatabaseIQN extends BaseModel {

    @Required
    @MaxSize(255)
    @Column(name="origin_iqn", nullable = false, length = 255)
    public String originIQN;


    @Required
    @MaxSize(255)
    @Column(name="destination_iqn", nullable = false, length = 255)
    public String destinationIQN;
}
