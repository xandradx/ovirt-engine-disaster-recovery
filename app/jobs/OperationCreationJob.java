package jobs;

import models.DisasterRecoveryOperation;
import models.RemoteHost;
import play.jobs.Job;

/**
 * Created by jandrad on 23/01/16.
 */
public class OperationCreationJob extends Job {

    private RemoteHost.RecoveryType type;

    public OperationCreationJob(RemoteHost.RecoveryType type) {
        this.type = type;
    }

    @Override
    public DisasterRecoveryOperation doJobWithResult() throws Exception {
        DisasterRecoveryOperation operation = new DisasterRecoveryOperation(type);
        operation.save();

        return operation;
    }
}
