package com.atlassian.stash.ssh;

import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.stash.ssh.api.SshKey;
import javax.annotation.Nonnull;

@AsynchronousPreferred
public class SshKeyCreatedEvent
  extends SshKeyEvent
{
  public SshKeyCreatedEvent(@Nonnull Object source, @Nonnull SshKey key)
  {
    super(source, key);
  }
}
