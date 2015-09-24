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

package ut.com.edwardawebb.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lmig.forge.stash.ssh.rest.KeyDetailsResource;
import com.lmig.forge.stash.ssh.rest.KeyDetailsResourceModel;

public class KeyDetailsResourceTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {
        KeyDetailsResource resource = new KeyDetailsResource(null, null);

        Response response = resource.getMessage();
        final KeyDetailsResourceModel message = (KeyDetailsResourceModel) response.getEntity();

        assertEquals("wrong message","Hello World",message.getMessage());
    }
}
