package com.edwardawebb.stash.ssh.scheduler;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.edwardawebb.stash.ssh.EnterpriseSshKeyService;


public class KeyRotationScheduler implements DisposableBean, InitializingBean { 

     private static final JobId JOB_ID = JobId.of("com.edwardawebb.stash:stash-ssh-key-enforcer:KeyRotationJob"); 
     //private static final long JOB_INTERVAL = TimeUnit.DAYS.toMillis(1L);
     private static final long JOB_INTERVAL = TimeUnit.MINUTES.toMillis(1L);
     private static final String JOB_RUNNER_KEY = "com.edwardawebb.stash:stash-ssh-key-enforcer:KeyRotationJobRunner"; 
     private static final Logger log = LoggerFactory.getLogger(KeyRotationScheduler.class);
     
     private final SchedulerService schedulerService;
     private final EnterpriseSshKeyService enterpriseKeyService; 
     
     public KeyRotationScheduler(SchedulerService schedulerService,EnterpriseSshKeyService enterpriseKeyService) { 
         this.schedulerService = schedulerService; 
         this.enterpriseKeyService = enterpriseKeyService;
     } 

     @Override 
     public void afterPropertiesSet() throws SchedulerServiceException { 
         //The JobRunner could be another component injected in the constructor, a 
         //private nested class, etc. It just needs to implement JobRunner 
         schedulerService.registerJobRunner(JobRunnerKey.of(JOB_RUNNER_KEY), new KeyRotationJobRunner(enterpriseKeyService)); 
         schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey(JobRunnerKey.of(JOB_RUNNER_KEY)) 
                 .withRunMode(RunMode.RUN_ONCE_PER_CLUSTER) 
                 .withSchedule(Schedule.forInterval(JOB_INTERVAL, new Date(System.currentTimeMillis() + JOB_INTERVAL)))); 
         log.warn("KEY Expiring Job Scheduled");
     } 

     @Override 
     public void destroy() { 
         schedulerService.unregisterJobRunner(JobRunnerKey.of(JOB_RUNNER_KEY)); 
     } 
 }