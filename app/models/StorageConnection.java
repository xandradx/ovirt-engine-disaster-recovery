package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by jandrad on 23/01/16.
 */
@Entity
public class StorageConnection extends BaseModel {

    @Required
    @MaxSize(20)
    @Column(name="origin_ip", nullable = false, length = 20)
    public String originIp;

    @Required
    @MaxSize(255)
    @Column(name="origin_iqn", nullable = false, length = 255)
    public String originIqn;

    @Required
    @MaxSize(20)
    @Column(name="destination_ip", nullable = false, length = 20)
    public String destinationIp;

    @Required
    @MaxSize(255)
    @Column(name="destination_iqn", nullable = false, length = 255)
    public String destinationIqn;


}
