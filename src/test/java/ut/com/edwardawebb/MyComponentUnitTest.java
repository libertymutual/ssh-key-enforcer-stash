package ut.com.edwardawebb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.edwardawebb.MyPluginComponent;
import com.edwardawebb.MyPluginComponentImpl;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}