/*
 *   Copyright 2016 ITM, S.A.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * `*   `[`http://www.apache.org/licenses/LICENSE-2.0`](http://www.apache.org/licenses/LICENSE-2.0)
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
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
