package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by jandrad on 23/01/16.
 */

@Entity
public class OperationLog extends Model {

    public enum MessageType {
        MESSAGE,
        ERROR
    }

    public OperationLog(MessageType type) {
        this.date = new Date();
        this.type = type;
    }

    public MessageType type;

    public Date date;

    @ManyToOne
    public DisasterRecoveryOperation operation;

    public String message;
}
