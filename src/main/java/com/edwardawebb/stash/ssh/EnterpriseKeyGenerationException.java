package com.edwardawebb.stash.ssh;

import java.security.NoSuchAlgorithmException;

public class EnterpriseKeyGenerationException extends RuntimeException {

    public EnterpriseKeyGenerationException(Exception e) {
        super(e);
    }

}
