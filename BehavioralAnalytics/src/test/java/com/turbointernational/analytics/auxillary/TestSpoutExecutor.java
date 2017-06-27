package com.turbointernational.analytics.auxillary;

/**
 * Created by kshakirov on 6/26/17.
 */

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.storm.tuple.Values;
import org.json.simple.JSONObject;

/**
 * Unit test for simple App.
 */
public class TestSpoutExecutor
        extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestSpoutExecutor( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( com.turbointernational.analytics.auxillary.TestSpoutExecutor.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testExecutor()
    {
        SpoutExecutor spoutExecutor = new SpoutExecutor();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "visitor");
        jsonObject.put("id", 576879073);
        Values values = spoutExecutor.execute(jsonObject);
        assertNotNull( values );

    }
}

