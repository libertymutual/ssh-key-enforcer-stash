package it.com.edwardawebb.rest;

import static org.junit.Assert.assertEquals;

import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.edwardawebb.rest.KeyDetailsResourceModel;

public class KeyDetailsResourceFuncTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {

        String baseUrl = System.getProperty("baseurl");
        String resourceUrl = baseUrl + "/rest/keydetailsresource/1.0/message";

        RestClient client = new RestClient();
        Resource resource = client.resource(resourceUrl);

        KeyDetailsResourceModel message = resource.get(KeyDetailsResourceModel.class);

        assertEquals("wrong message","Hello World",message.getMessage());
    }
}
