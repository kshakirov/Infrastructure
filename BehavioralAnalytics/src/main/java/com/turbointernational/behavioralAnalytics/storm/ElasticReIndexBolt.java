package com.turbointernational.behavioralAnalytics.storm;

import com.turbointernational.behavioralAnalytics.auxillary.BoltUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by kshakirov on 6/23/17.
 */
public class ElasticReIndexBolt extends BaseRichBolt {
    private String elasticHost;
    private String elasticIndex;
    private OutputCollector collector;
    private static final Logger LOG = LoggerFactory.getLogger(ElasticReIndexBolt.class);

    public ElasticReIndexBolt(String elasticHost, String elasticIndex) {
        this.elasticHost = elasticHost;
        this.elasticIndex = elasticIndex;
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String streamId = tuple.getSourceStreamId();
        if (BoltUtils.isCustomerLog(streamId)) {
            LOG.info("Customer");
        } else {
            LOG.info("Visitor");
        }

        collector.ack(tuple);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
