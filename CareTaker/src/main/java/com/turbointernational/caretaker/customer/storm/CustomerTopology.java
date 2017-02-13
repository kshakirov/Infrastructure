package com.turbointernational.caretaker.customer.storm;


import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by kshakirov on 2/6/17.
 */
public class CustomerTopology {

    private static StormTopology createTopology(){
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", new RabbitSpout(), 1);
        builder.setBolt("forgotten", new CustomerPasswordBolt(), 1).shuffleGrouping("spout","forgottenPassword");
        builder.setBolt("mailPassword", new CustomerMailBolt(), 1).shuffleGrouping("forgotten");
        return builder.createTopology();
    }

    private static Config creatConfig(){
        Config conf = new Config();
        conf.setDebug(false);
        conf.setMaxTaskParallelism(1);
        return conf;
    }

    public static void submitRemoteCluster(String name) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {

        StormSubmitter.submitTopology(name, creatConfig(),	createTopology());
    }

    public static void submitLocalCluster(String name){

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(name,creatConfig(), createTopology());
    }

    public static void main( String[] args ) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {

        System.out.println( "Customer Topology started" );
        submitLocalCluster("customerTopology");
        //submitRemoteCluster("customerTopology");


    }
}
