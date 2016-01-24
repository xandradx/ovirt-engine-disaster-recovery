package jobs;

import models.UserRole;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class StartupJob extends Job {

	@Override
	public void doJob() throws Exception {
		if (UserRole.count() == 0) {
			Fixtures.loadModels("initial-data.yml");
		}
	}
}
