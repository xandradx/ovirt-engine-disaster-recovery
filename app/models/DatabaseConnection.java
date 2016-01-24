package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by jandrad on 23/01/16.
 */
@Entity
public class DatabaseConnection extends BaseModel {

    @Required
    @MaxSize(20)
    @Column(name="origin_connection", nullable = false, length = 20)
    public String originConnection;


    @Required
    @MaxSize(20)
    @Column(name="destination_connection", nullable = false, length = 20)
    public String destinationConnection;

}
