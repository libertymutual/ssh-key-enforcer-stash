package it.com.lmig.forge.stash.ssh.rest;

import static org.junit.Assert.assertEquals;

import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lmig.forge.stash.ssh.rest.AdminConfigResourceModel;

public class AdminConfigResourceFuncTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {

        String baseUrl = System.getProperty("baseurl");
        String resourceUrl = baseUrl + "/rest/adminconfig/1.0/message";

        RestClient client = new RestClient();
        Resource resource = client.resource(resourceUrl);

        AdminConfigResourceModel message = resource.get(AdminConfigResourceModel.class);

        assertEquals("wrong message","Hello World",message.getMessage());
    }
}
