package com.edwardawebb.stash;

import org.apache.log4j.Logger;

import com.atlassian.event.api.EventListener;
import com.atlassian.stash.event.StashEvent;

public class GeneralEventListener {
    private final Logger logger = Logger.getLogger(GeneralEventListener.class);
    
    private final static String SSH_KEY_CREATED_EVENT_CLASS="com.atlassian.stash.ssh.SshKeyCreatedEvent";
    
    @EventListener
    public void mylistener(StashEvent stashEvent)
    {
        logger.warn("=="+stashEvent.getClass().toString());
        if(SSH_KEY_CREATED_EVENT_CLASS == stashEvent.getClass().getCanonicalName()){
            logger.warn("SSH key was created by" + stashEvent.getUser().getDisplayName());
            
            // cant cancel and can't interact withthe UI..
            // replace with one from service and email user?
        }
        
    }

    
}
