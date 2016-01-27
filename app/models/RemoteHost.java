package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by jandrad on 23/01/16.
 */

@Entity
public class RemoteHost extends BaseModel {

    public enum RecoveryType {
        FAILOVER,
        FAILBACK,
        NONE
    }

    @Required
    @Column(nullable = false)
    public RecoveryType type;

    @Required
    @MaxSize(255)
    @Column(length=255, name="host_name", nullable = false)
    public String hostName;
}
