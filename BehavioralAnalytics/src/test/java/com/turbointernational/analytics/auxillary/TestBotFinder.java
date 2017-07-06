package com.turbointernational.analytics.auxillary;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by kshakirov on 6/29/17.
 */
public class TestBotFinder {
    private Session session;
    private BotFinderBoltExecutor executor;
    @Before
    public void setUp(){
        Cluster cluster = Cluster.builder()                                                    // (1)
                .addContactPoints(new String[]{"10.1.3.15", "10.1.3.16", "10.1.3.17"})
                .build();
        session = cluster.connect("turbo_development");                                           // (2)
        executor = new BotFinderBoltExecutor(session, 60L);

    }

    @Test
    public void testExecutor(){
        executor.execute(576879073L, 1L);
    }

    @Test
    public void testReportBot(){
        executor.reportBot(1234L);
    }

    @Test
    public void testStatProductRank(){
        executor.statProductRank(1L);
    }
}
