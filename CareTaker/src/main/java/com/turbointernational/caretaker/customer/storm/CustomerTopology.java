package com.turbointernational.caretaker.customer.storm;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by kshakirov on 2/6/17.
 */
public class CustomerTopology {

    private static StormTopology createTopology() {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", new RabbitSpout(System.getProperty("rabbitHost")), 1);
        builder.setBolt("forgotten", new CustomerPasswordBolt(System.getProperty("turboHost"),
                        System.getProperty("turboHostPort"),System.getProperty("token")),
                1).shuffleGrouping("spout", "forgottenPassword");
        builder.setBolt("mailPassword",
                new CustomerMailBolt(System.getProperty("admin_email"), System.getProperty("admin_email_password"),
                        System.getProperty("admin_smtp"), System.getProperty("hostDnsName")), 1)
                .shuffleGrouping("forgotten", "forgottenPassword");
        builder.setBolt("messageLog",  new MessageLogBolt(System.getProperty("turboHost"),
                        System.getProperty("turboHostPort"), System.getProperty("token")), 1)
                .shuffleGrouping("mailPassword", "forgottenPassword");
        return builder.createTopology();
    }

    private static Config creatConfig() {
        Config conf = new Config();
        conf.setDebug(false);
        conf.setMaxTaskParallelism(1);
        return conf;
    }

    public static void submitRemoteCluster(String name) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {

        StormSubmitter.submitTopology(name, creatConfig(), createTopology());
    }

    public static void submitLocalCluster(String name) {

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(name, creatConfig(), createTopology());
    }

    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {

        System.out.println("Customer Topology started");
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

                if (commandLine.hasOption("remote")) {
                    System.out.println("REMOTE");
                    submitRemoteCluster("CustomerTopology");
                } else {
                    submitLocalCluster("customerTopology");
                }

            } else {
                System.out.println("Supply parameters: Rabbit host, Turbo Host");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
