package com.edwardawebb.stash.ssh.crypto;

import java.io.UnsupportedEncodingException;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.edwardawebb.stash.ssh.EnterpriseKeyGenerationException;
import com.edwardawebb.stash.ssh.rest.KeyPairResourceModel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;

public class JschSshKeyPairGenerator implements SshKeyPairGenerator {

    @Override
    public KeyPairResourceModel generateKeyPair(String comment) {
        JSch jsch=new JSch();
        KeyPairResourceModel result = new KeyPairResourceModel();
        KeyPair kpair;
        
        try {
            kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
            result.setFingerprint(kpair.getFingerPrint());
            ByteArrayOutputStream pubos = new ByteArrayOutputStream();
            kpair.writePublicKey(pubos, comment);
            result.setPublicKey(pubos.toString("UTF-8"));
            ByteArrayOutputStream privos = new ByteArrayOutputStream();
            kpair.writePrivateKey(privos);
            result.setPrivateKey(privos.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new EnterpriseKeyGenerationException(e);
        } catch (Exception e) {
            throw new EnterpriseKeyGenerationException(e);
        }
        kpair.dispose();
        return result;
    }

}
