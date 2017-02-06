package com.turbointernational.caretaker.customer.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import com.turbointernational.tutorial.RandomSentenceSpout;
import com.turbointernational.tutorial.WordCountBolt;

/**
 * Created by kshakirov on 2/6/17.
 */
public class CustomerTopology {
    public static void main( String[] args )
    {

        System.out.println( "Customer Topology started" );
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", new RabbitSpout(), 1);

        builder.setBolt("count", new CustomerPasswordBolt(), 1).shuffleGrouping("spout");
        Config conf = new Config();

        conf.setDebug(true);
        conf.setMaxTaskParallelism(1);
        String topologyName = "word-count";
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(topologyName,conf,builder.createTopology());


    }
}
