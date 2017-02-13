package com.turbointernational.tutorial;


import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Hello world!
 *
 */
public class CareTakerTopology
{


    public static void main( String[] args )
    {

        System.out.println( "Hello World!" );
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", new RandomSentenceSpout(), 5);

        builder.setBolt("count", new WordCountBolt(), 12).shuffleGrouping("spout");
        Config conf = new Config();

        conf.setDebug(true);
        conf.setMaxTaskParallelism(3);
        String topologyName = "word-count";
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(topologyName,conf,builder.createTopology());


    }
}
