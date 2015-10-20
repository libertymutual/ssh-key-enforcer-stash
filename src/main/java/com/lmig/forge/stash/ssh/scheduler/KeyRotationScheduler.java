/*
 * Copyright 2015, Liberty Mutual Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lmig.forge.stash.ssh.scheduler;

import java.util.Date;
import java.util.List;

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
import com.atlassian.scheduler.status.JobDetails;
import com.lmig.forge.stash.ssh.config.PluginSettingsService;


public class KeyRotationScheduler implements DisposableBean, InitializingBean { 

     private static final JobId JOB_ID = JobId.of("com.lmig.forge.stash:stash-ssh-key-enforcer:KeyRotationJob"); 
     private static final String JOB_RUNNER_KEY = "com.lmig.forge.stash:stash-ssh-key-enforcer:KeyRotationJobRunner"; 
     private static final Logger log = LoggerFactory.getLogger(KeyRotationScheduler.class);
     
     private final SchedulerService schedulerService;
    private final KeyRotationJobRunner keyRotationJobRunner;
    private final PluginSettingsService pluginSettingsService; 
     
     public KeyRotationScheduler(SchedulerService schedulerService,KeyRotationJobRunner keyRotationJobRunner,PluginSettingsService pluginSettingsService) { 
         this.schedulerService = schedulerService; 
         this.keyRotationJobRunner = keyRotationJobRunner;
         this.pluginSettingsService  = pluginSettingsService;
     } 

     @Override 
     /**
      * this occurs on start and when the settings/config of server are updated. Includes any changes made via {@link PluginSettingsService}
      */
     public void afterPropertiesSet() throws SchedulerServiceException {   
         long runInterval = pluginSettingsService.getMillisBetweenRuns();
       //  long runInterval = 60000; //for live demos
         if(runInterval > 0){
             schedulerService.registerJobRunner(JobRunnerKey.of(JOB_RUNNER_KEY), keyRotationJobRunner); 
             schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey(JobRunnerKey.of(JOB_RUNNER_KEY)) 
                     .withRunMode(RunMode.RUN_ONCE_PER_CLUSTER) 
                     .withSchedule(Schedule.forInterval(runInterval, new Date(System.currentTimeMillis() + runInterval)))); 
             log.warn("KEY Expiring Job Scheduled");
         }else{
            List<JobDetails> existingSchedules =  schedulerService.getJobsByJobRunnerKey(JobRunnerKey.of(JOB_RUNNER_KEY));
            if( existingSchedules.size() > 0 ){
                schedulerService.unregisterJobRunner(JobRunnerKey.of(JOB_RUNNER_KEY));
            }
         }
     } 

     @Override 
     public void destroy() { 
         schedulerService.unregisterJobRunner(JobRunnerKey.of(JOB_RUNNER_KEY)); 
         log.debug("KEY Expiring Job unregistered");
     } 
 }