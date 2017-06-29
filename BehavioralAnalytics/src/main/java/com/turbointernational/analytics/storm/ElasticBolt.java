package com.turbointernational.analytics.storm;

import com.turbointernational.analytics.auxillary.CassandraEnv;
import com.turbointernational.analytics.auxillary.ElasticBoltExecutor;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kshakirov on 6/23/17.
 */
public class ElasticBolt extends BaseRichBolt {
    private String elasticHost;
    private String elasticIndex;
    private OutputCollector collector;
    private static final Logger LOG = LoggerFactory.getLogger(ElasticBolt.class);
    private ElasticBoltExecutor executor;

    public ElasticBolt(String environment) {
        this.elasticHost = CassandraEnv.valueOf(environment.toUpperCase()).elsticHosts()[0];
        this.elasticIndex = CassandraEnv.valueOf(environment.toUpperCase()).elasticIndex();
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        executor = new ElasticBoltExecutor(elasticHost, elasticIndex);
    }

    @Override
    public void execute(Tuple tuple) {
        String type = tuple.getStringByField("type");
        executor.execute(tuple.getValue(1));
        collector.ack(tuple);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream("visitor_logs", new Fields("visitor_id", "entity"));
    }
}
