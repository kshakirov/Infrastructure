package com.turbointernational.analytics.auxillary;

import com.datastax.driver.core.*;
import junit.framework.Assert;
import junit.framework.Test.*;
import org.apache.storm.tuple.Values;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;


/**
 * Created by kshakirov on 6/26/17.
 */
public class TestCassandraBoltExecutor {
    private Session session;
    private String cassandraHost = "10.1.3.16";
    private String keyspace = "turbo_development";
    private CassandraBoltExecutor executor;

    @Before
    public void setUp() {
       Cluster     cluster = Cluster.builder()                                                    // (1)
                    .addContactPoints(new String[]{"10.1.3.15", "10.1.3.16", "10.1.3.17"})
                    .build();
            KeyspaceMetadata metadata = cluster.getMetadata().getKeyspace("turbo_development");
            session = cluster.connect(keyspace);                                           // (2)
        executor = new CassandraBoltExecutor(session);
    }

    @Test
    public void testGetVisitor() {
        String type = "visitor_logs";
        Long primaryKey = Long.valueOf(576879073);
        Values values = executor.execute(primaryKey,type);
        Assert.assertNotNull(values);
    }
}
