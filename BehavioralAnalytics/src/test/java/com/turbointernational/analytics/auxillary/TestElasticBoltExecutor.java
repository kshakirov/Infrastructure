package com.turbointernational.analytics.auxillary;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import junit.framework.Assert;
import org.apache.storm.shade.org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by kshakirov on 6/27/17.
 */
public class TestElasticBoltExecutor {
    private ElasticBoltExecutor executor;
    @Before
    public  void setUp(){
        executor = new ElasticBoltExecutor("10.1.3.15","development_turbo_analytics");
    }
    @Test
    public void testCreateIndex(){
        try {
            JestResult result = executor.createIndex();
            Assert.assertNotNull(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testIndexVisitorLog(){
        VisitorLog visitorLog = new VisitorLog();
        visitorLog.setVisitorId(Long.valueOf(12345));
        visitorLog.setId(UUID.randomUUID());
        visitorLog.setDate(DateTime.now().toDate());
        executor.indexVisitorLog(visitorLog);
    }
}
