package jobs;

import models.DisasterRecoveryOperation;
import models.UserRole;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

import java.util.List;

@OnApplicationStart
public class StartupJob extends Job {

	@Override
	public void doJob() throws Exception {
		if (UserRole.count() == 0) {
			Fixtures.loadModels("initial-data.yml");
		}


        List<DisasterRecoveryOperation> operations = DisasterRecoveryOperation.find("active = ? AND status = ?", true, DisasterRecoveryOperation.OperationStatus.PROGRESS).fetch();
        for (DisasterRecoveryOperation operation : operations) {
            operation.status = DisasterRecoveryOperation.OperationStatus.FAILED;
            operation.save();
        }

	}
}
