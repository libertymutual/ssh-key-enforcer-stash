package com.edwardawebb.stash;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.atlassian.event.api.EventListener;
import com.atlassian.stash.event.StashEvent;
import com.atlassian.stash.ssh.api.SshKey;

public class GeneralEventListener {
    private final Logger logger = Logger.getLogger(GeneralEventListener.class);
    
    private final static String SSH_KEY_CREATED_EVENT_CLASS="com.atlassian.stash.ssh.SshKeyCreatedEvent";
    
    @EventListener
    public void mylistener(StashEvent stashEvent)
    {
        logger.warn("=="+stashEvent.getClass().toString());
        
        // THIS is some BS..  Reflection works using our own class that mimics Atlassian object. BUT this cannot be casted to the object (ours or atlassians)
        // https://developer.atlassian.com/static/javadoc/stash/3.11.2/ssh/reference/com/atlassian/stash/ssh/SshKeyCreatedEvent.html
        // https://maven.atlassian.com/#nexus-search;classname~com.atlassian.stash.ssh.SshKeyCreatedEvent
        // Dependency included as provided compiles, but fails run time with no class def
        // Dependeny included as compile and it compiles, but fails at run time with some other class failing..

            logger.warn("SSH key was created by" + stashEvent.getUser().getDisplayName());
            
            
            
            try {
                Method method = stashEvent.getClass().getMethod("getKey");
                SshKey key = (SshKey) method.invoke(stashEvent);
                
                logger.warn("The SSH Key is: " + key.getLabel() + "|with value: " + key.getText() + "|with user: " + key.getUser());
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }

    
}
