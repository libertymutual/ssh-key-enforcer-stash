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

package com.lmig.forge.stash.ssh.crypto;

import java.io.UnsupportedEncodingException;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import com.lmig.forge.stash.ssh.EnterpriseKeyGenerationException;
import com.lmig.forge.stash.ssh.rest.KeyPairResourceModel;

public class JschSshKeyPairGenerator implements SshKeyPairGenerator {

    private static int KEY_SIZE = 2048;
    
    @Override
    public KeyPairResourceModel generateKeyPair(String comment) {
        JSch jsch=new JSch();
        KeyPairResourceModel result = new KeyPairResourceModel();
        KeyPair kpair;
        
        try {
            kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA, KEY_SIZE);
            result.setFingerprint(kpair.getFingerPrint());
            ByteArrayOutputStream pubos = new ByteArrayOutputStream();
            kpair.writePublicKey(pubos, comment);
            pubos.close();
            result.setPublicKey(new String(pubos.toByteArray()));
            ByteArrayOutputStream privos = new ByteArrayOutputStream();
            kpair.writePrivateKey(privos);
            privos.close();
            result.setPrivateKey(new String(privos.toByteArray()));
        } catch (UnsupportedEncodingException e) {
            throw new EnterpriseKeyGenerationException(e);
        } catch (Exception e) {
            throw new EnterpriseKeyGenerationException(e);
        }
        kpair.dispose();
        return result;
    }

}
