package models;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jandrad on 23/01/16.
 */
@Entity
public class DisasterRecoveryOperation extends BaseModel {

    public enum OperationStatus {
        PROGRESS,
        SUCCESS,
        FAILED
    }

    public DisasterRecoveryOperation(RemoteHost.RecoveryType type) {
        this.status = OperationStatus.PROGRESS;
        this.type = type;
    }

    public RemoteHost.RecoveryType type;

    @OneToMany(mappedBy = "operation")
    public List<OperationLog> logs = new ArrayList<OperationLog>();

    public OperationStatus status;

    public void addMessageLog(String message) {
        addLog(OperationLog.MessageType.MESSAGE, message);
    }

    public void addErrorLog(String message) {
        addLog(OperationLog.MessageType.ERROR, message);
    }

    private void addLog(OperationLog.MessageType type, String message) {
        OperationLog log = new OperationLog(type);
        log.message = message;
        log.operation = this;
        log.save();
    }

}
