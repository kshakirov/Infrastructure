package com.turbointernational.behavioralAnalytics.storm;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * Created by kshakirov on 6/23/17.
 */
public class CassandraBolt  extends BaseRichBolt{

    private String cassandraHost;
    private String cassandraVisitorTableName;
    private String cassandraCustomerTableName;
    private OutputCollector collector;
    private Session session;

    public CassandraBolt(String cassandraHost, String cassandraVisitorTableName, String cassandraCustomerTableName){
        this.cassandraHost= cassandraHost;
        this.cassandraVisitorTableName=cassandraVisitorTableName;
        this.cassandraCustomerTableName = cassandraCustomerTableName;

    }
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        Cluster cluster = null;
        try {
            cluster = Cluster.builder()                                                    // (1)
                    .addContactPoint(cassandraHost)
                    .build();
            session = cluster.connect();                                           // (2)
;                          // (4)
        } finally {
            if (cluster != null) cluster.close();                                          // (5)
        }
    }

    @Override
    public void execute(Tuple tuple) {
        String streamId = tuple.getSourceStreamId();
        getLog((Long) tuple.getValueByField("id"));
        collector.ack(tuple);
        //collector.emit("", new Values());

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream("customerLog", new Fields("id", "data"));
        outputFieldsDeclarer.declareStream("visitorLog", new Fields("id", "data"));
    }

    private void getLog(Long id){
        ResultSet rs = session.execute("Select * from visitor_logs Where visitor_id=" + id);    // (3)
        Row row = rs.one();
        System.out.println(row.getString("product"));
    }
}
