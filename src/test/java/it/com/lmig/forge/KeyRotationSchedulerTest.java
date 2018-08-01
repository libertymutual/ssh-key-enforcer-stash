package it.com.lmig.forge;

import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.status.JobDetails;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(AtlassianPluginsTestRunner.class)
public class KeyRotationSchedulerTest {
    private static final String JOB_RUNNER_KEY = "com.lmig.forge.stash:stash-ssh-key-enforcer:KeyRotationJobRunner";
    private final SchedulerService schedulerService;

    public KeyRotationSchedulerTest(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Test
    public void testJobIsScheduled() {
        List<JobDetails> scheduledJobs = schedulerService.getJobsByJobRunnerKey(JobRunnerKey.of(JOB_RUNNER_KEY));
        assertEquals(1, scheduledJobs.size());
    }
}
