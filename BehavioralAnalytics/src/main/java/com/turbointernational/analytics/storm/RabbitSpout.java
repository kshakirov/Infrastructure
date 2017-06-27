package com.turbointernational.analytics.storm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.turbointernational.analytics.auxillary.SpoutExecutor;
import com.turbointernational.analytics.auxillary.SpoutUtils;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Created by kshakirov on 6/23/17.
 */
public class RabbitSpout extends BaseRichSpout {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitSpout.class);
    private String QUEUE_NAME;
    SpoutOutputCollector _collector;
    private String rabbitHost;
    Random _rand;
    private static Channel channel;
    private QueueingConsumer consumer;
    private Connection connection;
    private SpoutExecutor spoutExecutor;

    public RabbitSpout(String rabbitHost, String queue_name) {
        this.rabbitHost = rabbitHost;
        this.QUEUE_NAME = queue_name;

    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
        _rand = new Random();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitHost);
        connection = null;
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        try {
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            consumer = new QueueingConsumer(channel);
            channel.basicConsume(QUEUE_NAME, false, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        }
        spoutExecutor = new SpoutExecutor();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("message", new Fields("type","id"));
    }

    @Override
    public void nextTuple() {

        QueueingConsumer.Delivery delivery = null;

        try {
            delivery = consumer.nextDelivery();
            JSONObject message = SpoutUtils.readMessage(new String(delivery.getBody()));
            Values values = spoutExecutor.execute(message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            _collector.emit("message",values);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void close() {
        LOG.info("close");
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.close();
    }

}
