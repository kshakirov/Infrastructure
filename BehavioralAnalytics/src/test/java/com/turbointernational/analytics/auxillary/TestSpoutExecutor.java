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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;

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
        JSONParser parser = new JSONParser();
        try {
           jsonObject = (JSONObject) parser.parse("{\"type\":\"visitor_logs\",  \"data\":  {  \"visitor_id\":  576879073, \"date\": 1498484873916, \"id\": \"0d9d8fc0-5a76-11e7-aa4e-59c6221f711e\", \"ip\": \"127.0.0.1\" }}");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Values values = spoutExecutor.execute(jsonObject);
        assertNotNull( values );

    }
}

