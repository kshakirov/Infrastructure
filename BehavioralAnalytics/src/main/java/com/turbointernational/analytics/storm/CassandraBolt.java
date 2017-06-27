package com.turbointernational.analytics.storm;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.turbointernational.analytics.auxillary.CassandraBoltExecutor;
import com.turbointernational.analytics.auxillary.CassandraEnv;
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
public class CassandraBolt extends BaseRichBolt {

    private OutputCollector collector;
    private Session session;
    private String environment;
    private String[] cassandraHosts;
    private CassandraBoltExecutor cassandraBoltExecutor;

    public CassandraBolt(String environement) {
        this.environment  = CassandraEnv.valueOf(environement.toUpperCase()).keySpace();
        this.cassandraHosts = CassandraEnv.valueOf(environement.toUpperCase()).hosts();

    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        Cluster cluster = Cluster.builder()
                .addContactPoints(cassandraHosts)
                .build();
        session = cluster.connect(environment);
        cassandraBoltExecutor = new CassandraBoltExecutor(session);
    }

    @Override
    public void execute(Tuple tuple) {
        String type = tuple.getStringByField("type");
        Long id = tuple.getLongByField("id");
        Values values = cassandraBoltExecutor.execute(id,type);
        collector.ack(tuple);
        collector.emit(values);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream("entity", new Fields("id", "entity"));
    }




}
