package jobs;

import models.DisasterRecoveryOperation;
import play.jobs.Job;

/**
 * Created by jandrad on 23/01/16.
 */
public class OperationUpdateJob extends Job {

    private DisasterRecoveryOperation.OperationStatus status;
    private long operationId;

    public OperationUpdateJob(long operationId, DisasterRecoveryOperation.OperationStatus status) {
        this.operationId = operationId;
        this.status = status;
    }

    @Override
    public void doJob() throws Exception {
        DisasterRecoveryOperation operation = DisasterRecoveryOperation.findById(operationId);
        if (operation!=null) {
            operation.status = status;
            operation.save();
        }
    }
}