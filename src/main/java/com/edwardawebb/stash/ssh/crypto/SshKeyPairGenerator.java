package com.edwardawebb.stash.ssh.crypto;

import com.atlassian.stash.user.StashUser;
import com.edwardawebb.rest.KeyPairResourceModel;

public interface SshKeyPairGenerator {

    KeyPairResourceModel generateKeyPair(String comment);
}
