package com.atlassian.stash.ssh;

import javax.annotation.Nonnull;

import com.atlassian.stash.event.StashEvent;
import com.atlassian.stash.ssh.api.SshKey;
import com.google.common.base.Preconditions;


public abstract class SshKeyEvent
  extends StashEvent
{
  private final SshKey key;
  
  protected SshKeyEvent(@Nonnull Object source, @Nonnull SshKey key)
  {
    super(source);
    
    this.key = ((SshKey)Preconditions.checkNotNull(key, "key"));
  }
  
  @Nonnull
  public SshKey getKey()
  {
    return this.key;
  }
}
