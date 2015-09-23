package ut.com.edwardawebb.stash.ssh.scheduler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import net.java.ao.test.jdbc.NonTransactional;

import org.junit.Before;
import org.junit.Test;

import com.edwardawebb.stash.ssh.keys.EnterpriseSshKeyServiceImpl;
import com.edwardawebb.stash.ssh.scheduler.KeyRotationJobRunner;




public class KeyRotationJobRunnerTest {
   
    
    @Before
    public void setup(){
    }
    
    @Test
    public void enterpriseServiceExpireFunctionIsCalledOnExecution(){
        EnterpriseSshKeyServiceImpl service = mock(EnterpriseSshKeyServiceImpl.class);

        KeyRotationJobRunner jobRunner = new KeyRotationJobRunner(service);
        jobRunner.runJob(null);
        
        verify(service).replaceExpiredKeysAndNotifyUsers();
        
    }
    
    
    
}
