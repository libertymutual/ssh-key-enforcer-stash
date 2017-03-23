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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
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

import javax.annotation.concurrent.GuardedBy;

/**
 * This class schedules jobs, namely the job that purges expired keys, sicne it depends on AO and other things being in place, it uses this
 * hack from atlassian: https://bitbucket.org/cfuller/atlassian-scheduler-jira-example/src/90a07c5af0fbd6272c034100923a50f892ce7d07/src/main/java/com/atlassian/jira/plugins/example/scheduler/impl/AwesomeLauncher.java?at=master&fileviewer=file-view-default#AwesomeLauncher.java-23
 */
public class KeyRotationScheduler implements LifecycleAware, DisposableBean, InitializingBean {

    private static final JobId JOB_ID = JobId.of("com.lmig.forge.stash:stash-ssh-key-enforcer:KeyRotationJob");
    private static final String JOB_RUNNER_KEY = "com.lmig.forge.stash:stash-ssh-key-enforcer:KeyRotationJobRunner";
    private static final Logger log = LoggerFactory.getLogger(KeyRotationScheduler.class);
    private static final String PLUGIN_KEY = "com.lmig.forge.stash.ssh.stash-ssh-key-enforcer";
    private final SchedulerService schedulerService;
    private final KeyRotationJobRunner keyRotationJobRunner;
    private final PluginSettingsService pluginSettingsService;
    private final EventPublisher eventPublisher;

    // enables locks on event tracking enumset http://jcip.net/annotations/doc/net/jcip/annotations/GuardedBy.html
    @GuardedBy("this")
    private final Set<LifecycleEvent> lifecycleEvents = EnumSet.noneOf(LifecycleEvent.class);
     
     public KeyRotationScheduler(SchedulerService schedulerService, KeyRotationJobRunner keyRotationJobRunner, PluginSettingsService pluginSettingsService, EventPublisher eventPublisher) {
         this.schedulerService = schedulerService; 
         this.keyRotationJobRunner = keyRotationJobRunner;
         this.pluginSettingsService  = pluginSettingsService;
         this.eventPublisher = eventPublisher;
     }


    private void registerListener()
    {
        log.info("registerListeners");
        eventPublisher.register(this);
    }

    private void unregisterListener()
    {
        log.info("unregisterListeners");
        eventPublisher.unregister(this);
    }
     private void unregisterJobRunner() {
         schedulerService.unregisterJobRunner(JobRunnerKey.of(JOB_RUNNER_KEY)); 
         log.info("KEY Expiring Job unregistered");
     }

    /**
     * This is received from Spring after the bean's properties are set.  We need to accept this to know when
     * it is safe to register an event listener.
     */
    @Override
    public void afterPropertiesSet()
    {
        registerListener();
        onLifecycleEvent(LifecycleEvent.AFTER_PROPERTIES_SET);
    }

    /**
     * This is received from SAL after the system is really up and running from its perspective.  This includes
     * things like the database being set up and other tricky things like that.  This needs to happen before we
     * try to schedule anything, or the scheduler's tables may not be in a good state on a clean install.
     */
    @Override
    public void onStart()
    {
        onLifecycleEvent(LifecycleEvent.LIFECYCLE_AWARE_ON_START);
    }

    @Override
    public void onStop() {

    }

    /**
     * This is received from the plugin system after the plugin is fully initialized.  It is not safe to use
     * Active Objects before this event is received.
     */
    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event)
    {
        if (PLUGIN_KEY.equals(event.getPlugin().getKey()))
        {
            onLifecycleEvent(LifecycleEvent.PLUGIN_ENABLED);
        }
    }


    /**
     * This is received from Spring when we are getting destroyed.  We should make sure we do not leave any
     * event listeners or job runners behind; otherwise, we could leak the current plugin context, leading to
     * exceptions from destroyed OSGi proxies, memory leaks, and strange behaviour in general.
     */
    @Override
    public void destroy() throws Exception
    {
        unregisterListener();
        unregisterJobRunner();
    }
    /**
     * The latch which ensures all of the plugin/application lifecycle progress is completed before we call
     * {@code launch()}.
     */
    private void onLifecycleEvent(LifecycleEvent event)
    {
        log.info("onLifecycleEvent: " + event);
        if (isLifecycleReady(event))
        {
            log.info("Got the last lifecycle event... Time to get started!");
            unregisterListener();

            try
            {
                registerJobRunner();  // AO and all other dependencies should be ready
            }
            catch (Exception ex)
            {
                log.error("Unexpected error during launch", ex);
            }
        }
    }

    /**
     * The event latch.
     * <p>
     * When something related to the plugin initialization happens, we call this with
     * the corresponding type of the event.  We will return {@code true} at most once, when the very last type
     * of event is triggered.  This method has to be {@code synchronized} because {@code EnumSet} is not
     * thread-safe and because we have multiple accesses to {@code lifecycleEvents} that need to happen
     * atomically for correct behaviour.
     * </p>
     *
     * @param event the lifecycle event that occurred
     * @return {@code true} if this completes the set of initialization-related events; {@code false} otherwise
     */
    synchronized private boolean isLifecycleReady(LifecycleEvent event)
    {
        return lifecycleEvents.add(event) && lifecycleEvents.size() == LifecycleEvent.values().length;
    }

    private void registerJobRunner() throws SchedulerServiceException {
        List<JobDetails> existingSchedules =  schedulerService.getJobsByJobRunnerKey(JobRunnerKey.of(JOB_RUNNER_KEY));
        if( existingSchedules.size() > 0 ){
            schedulerService.unregisterJobRunner(JobRunnerKey.of(JOB_RUNNER_KEY));
            log.info("Unregistered previously scheduled job");
        }
        long runInterval = pluginSettingsService.getMillisBetweenRuns();
        //  long runInterval = 60000; //for live demos
        if(runInterval > 0){
            schedulerService.registerJobRunner(JobRunnerKey.of(JOB_RUNNER_KEY), keyRotationJobRunner);
            schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey(JobRunnerKey.of(JOB_RUNNER_KEY))
                    .withRunMode(RunMode.RUN_ONCE_PER_CLUSTER)
                    .withSchedule(Schedule.forInterval(runInterval, new Date(System.currentTimeMillis() + 5000))));
            log.info("KEY Expiring Job Scheduled to run every " + runInterval + " ms. First run will trigger in ~5 seconds");
        }
    }



    /**
     * Used to keep track of everything that needs to happen before we are sure that it is safe
     * to talk to all of the components we need to use, particularly the {@code SchedulerService}
     * and Active Objects.  We will not try to initialize until all of them have happened.
     */
    static enum LifecycleEvent
    {
        AFTER_PROPERTIES_SET,
        PLUGIN_ENABLED,
        LIFECYCLE_AWARE_ON_START
    }

 }