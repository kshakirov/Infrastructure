package com.turbointernational.analytics.storm;

import org.apache.commons.cli.*;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by kshakirov on 6/23/17.
 */
public class AnalyticsTopology {

    private static StormTopology createTopology() {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("RabbitSpout", new RabbitSpout(System.getProperty("rabbitHost"),
                System.getProperty("rabbitQueue")), 1);
        builder.setBolt("CassandraBolt", new CassandraBolt(System.getProperty("environment")), 4).
                shuffleGrouping("RabbitSpout", "message");
        builder.setBolt("ElasticBolt", new ElasticBolt(System.getProperty("environment")), 4).
                shuffleGrouping("CassandraBolt", "entity").shuffleGrouping("BotFinderBolt", "entity");
        builder.setBolt("BotFinderBolt", new BotFinderBolt(System.getProperty("environment"),
                System.getProperty("bot_interval")), 4).
                shuffleGrouping("ElasticBolt", "bot_finder");



        return builder.createTopology();
    }

    public static void submitLocalCluster(String name) {
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(name, creatConfig(), createTopology());

    }

    public static void submitRemoteCluster(String name) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {

        StormSubmitter.submitTopology(name, creatConfig(), createTopology());
    }

    private static Config creatConfig() {
        Config conf = new Config();
        conf.setDebug(false);
        //conf.setMaxTaskParallelism(1);
        return conf;
    }

    public static void submitRemoteCluster() {

    }


    public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options()
                .addOption("properties", true, "Configuration properties")
                .addOption("remote", false, "Use remote cluster");
        try {
            CommandLineParser parser = new PosixParser();
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("properties")) {
                InputStream in = new FileInputStream(commandLine.getOptionValue("properties"));
                try {
                    Properties properties = new Properties(System.getProperties());
                    properties.load(in);
                    System.setProperties(properties);
                } finally {
                    in.close();
                }
                System.out.println(System.getProperty("topologyName") + "  started");

                if (commandLine.hasOption("remote")) {
                    submitRemoteCluster(System.getProperty("topologyName"));
                } else {
                    submitLocalCluster(System.getProperty("topologyName"));
                }

            } else {
                System.out.println("Supply parameters: Rabbit host, Turbo Host");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}