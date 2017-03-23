package ut.com.lmig.forge.stash.ssh.keys.scheduler;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.scheduler.SchedulerService;
import com.lmig.forge.stash.ssh.config.PluginSettingsService;
import com.lmig.forge.stash.ssh.rest.AdminConfigResourceModel;
import com.lmig.forge.stash.ssh.scheduler.KeyRotationJobRunner;
import com.lmig.forge.stash.ssh.scheduler.KeyRotationScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class KeyRotationSchedulerTest {
    private SchedulerService schedulerService = mock(SchedulerService.class);
    private KeyRotationJobRunner keyRotationJobRunner = mock(KeyRotationJobRunner.class);
    private PluginSettingsService pluginSettingsService = mock(PluginSettingsService.class);
    private EventPublisher eventPublisher = mock(EventPublisher.class);
    private PluginEnabledEvent pluginEnabledEvent = mock(PluginEnabledEvent.class,RETURNS_DEEP_STUBS);
    private KeyRotationScheduler scheduler;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        //instantiate class under test
        scheduler = new KeyRotationScheduler(schedulerService,keyRotationJobRunner,pluginSettingsService,eventPublisher);
        //access private constant needed to pass to class
        Field field = KeyRotationScheduler.class.getDeclaredField("PLUGIN_KEY");
        field.setAccessible(true);
        String pluginKey = (String)field.get(scheduler);
        //prepare mocks
        when(pluginEnabledEvent.getPlugin().getKey()).thenReturn(pluginKey);
        when(pluginSettingsService.getMillisBetweenRuns()).thenReturn(1000L);// must be >0 for scheduler to care
    }

    @After
    public void tearDown() {

    }

    /**
     * Before scheduling a job which depends on AO and other plugin system services, we wait for 3 critical events.
     * See {@link com.lmig.forge.stash.ssh.scheduler.KeyRotationScheduler}
     */
    @Test
    public void schedulerIsCalledAfterAllDependentEvents() {
        //when all defined events are received
        scheduler.afterPropertiesSet();
        scheduler.onStart();
        scheduler.onPluginEnabled(pluginEnabledEvent);

        //then the scheduler is called
        verify(schedulerService).registerJobRunner(anyObject(),anyObject());
    }

    @Test
    public void schedulerIsNotCalledAfterOnStartAlone() {
        // and onstart event is receieved
        scheduler.onStart();

        //then the scheduler is NOT called
        verify(schedulerService,never()).registerJobRunner(anyObject(),anyObject());
    }

    @Test
    public void schedulerIsNotCalledAfterPropertiesSetAlone() {
        // and propertiesset event is receieved
        scheduler.afterPropertiesSet();

        //then the scheduler is NOT called
        verify(schedulerService,never()).registerJobRunner(anyObject(),anyObject());
    }

    @Test
    public void schedulerIsNotCalledAfterPluginReadyAlone() {
        // and onstart event is receieved
        scheduler.onPluginEnabled(pluginEnabledEvent);

        //then the scheduler is NOT called
        verify(schedulerService,never()).registerJobRunner(anyObject(),anyObject());
    }


}




