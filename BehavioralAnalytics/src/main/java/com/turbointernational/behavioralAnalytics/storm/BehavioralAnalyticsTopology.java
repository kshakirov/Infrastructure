package com.turbointernational.behavioralAnalytics.storm;

import org.apache.commons.cli.*;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by kshakirov on 6/23/17.
 */
public class BehavioralAnalyticsTopology {

    private static StormTopology createTopology(){
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("RabbitSpout",new RabbitSpout(System.getProperty("rabbitHost"),
                System.getProperty("rabbitQueue")),1);
        builder.setBolt("CassandraBolt", new CassandraBolt(System.getProperty("cassandraHost"),
                System.getProperty("cassandraVisitorTableName"), System.getProperty("cassandraCustomerTableName")), 4).
                shuffleGrouping("RabbitSpout", "customerLog").shuffleGrouping("RabbitSpout", "visitorLog");

        return  builder.createTopology();
    }

    public static void submitLocalCluster(String name){
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(name, creatConfig(), createTopology());

    }

    private static Config creatConfig() {
        Config conf = new Config();
        conf.setDebug(false);
        conf.setMaxTaskParallelism(1);
        return conf;
    }

    public static void submitRemoteCluster(){

    }


    public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options()
                .addOption("properties", true, "Configuration properties")
                .addOption("remote", false, "Use remote cluster");
        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = parser.parse(options, args);
        InputStream in = new FileInputStream(commandLine.getOptionValue("properties"));
        try {
            Properties properties = new Properties(System.getProperties());
            properties.load(in);
            System.setProperties(properties);
        } finally {
            in.close();
        }
        submitLocalCluster("DevelopmentBehavioralAnalyticsTopology");


    }

}
