package com.turbointernational.analytics.auxillary;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import junit.framework.Assert;
import junit.framework.Test.*;
import org.apache.storm.shade.org.joda.time.*;
import org.apache.storm.tuple.Values;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;


/**
 * Created by kshakirov on 6/26/17.
 */
public class TestCassandraBoltExecutor {
    private Session session;
    private String cassandraHost = "10.1.3.16";
    private String keyspace = "turbo_development";
    private CassandraBoltExecutor executor;
    private HashMap visitorData;


    public HashMap createvisitorData() {
        HashMap visitorData = new HashMap();
        visitorData.put("visitor_id", 576879073L);
        visitorData.put("date", 1498484873916L);
        visitorData.put("id", "0d9d8fc0-5a76-11e7-aa4e-59c6221f711e");
        visitorData.put("ip", "127.0.0.1");
        return visitorData;
    }

    @Before
    public void setUp() {
        Cluster cluster = Cluster.builder()                                                    // (1)
                .addContactPoints(new String[]{"10.1.3.15", "10.1.3.16", "10.1.3.17"})
                .build();
        KeyspaceMetadata metadata = cluster.getMetadata().getKeyspace("turbo_development");
        session = cluster.connect(keyspace);                                           // (2)
        executor = new CassandraBoltExecutor(session);
        visitorData = createvisitorData();
    }

    @Test
    public void testGetVisitor() {
        String type = "visitor_logs";
        Values values = executor.execute(visitorData, type);
        Assert.assertNotNull(values);
    }


    @Test
    public void testMapper() {

        MappingManager manager = new MappingManager(session);
        Mapper<VisitorLog> mapper = manager.mapper(VisitorLog.class);

    }


}
