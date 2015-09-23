package com.edwardawebb.stash.ssh.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.edwardawebb.stash.ssh.keys.EnterpriseSshKeyService;

public class KeyRotationJobRunner implements JobRunner{

    
    private EnterpriseSshKeyService enterpriseKeyService;
    private static final Logger log = LoggerFactory.getLogger(KeyRotationJobRunner.class);
    
    public KeyRotationJobRunner(EnterpriseSshKeyService enterpriseKeyService) {
        this.enterpriseKeyService = enterpriseKeyService;
    }
    
    @Override
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        log.warn("Key Expire Job Starting");
        enterpriseKeyService.replaceExpiredKeysAndNotifyUsers();
        log.warn("Key Expire Job Complete");
        return JobRunnerResponse.success();
    }

}
