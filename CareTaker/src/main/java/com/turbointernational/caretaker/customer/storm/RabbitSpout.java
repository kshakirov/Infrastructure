package com.turbointernational.caretaker.customer.storm;


import com.rabbitmq.client.*;
import com.turbointernational.caretaker.customer.auxillary.SpoutUtils;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Created by kshakirov on 2/6/17.
 */
public class RabbitSpout extends BaseRichSpout

{
    private static final Logger LOG = LoggerFactory.getLogger(RabbitSpout.class);
    private final String QUEUE_NAME = "customer_email";
    SpoutOutputCollector _collector;
    private String rabbitHost;
    Random _rand;
    private static Channel channel;
    private QueueingConsumer consumer;
    private Connection connection;

    public RabbitSpout(String rabbitHost) {
        this.rabbitHost = rabbitHost;
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
    }


    @Override
    public void nextTuple() {

        try {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            JSONObject message = SpoutUtils.readMessage(new String(delivery.getBody()));
            String emailAddress = SpoutUtils.getEmailAddress(message);
            if (SpoutUtils.isForgottenPassword(message)) {
                LOG.info("Emitting forgotten email: " + (String) message.get("email"));
                _collector.emit("forgottenPassword", new Values(message.get("email")));
            } else if (SpoutUtils.isNewUser(message)) {
                LOG.info("Emitting new user email: " + (String) message.get("email"));
                _collector.emit("newUser", new Values(message.get("email")));
            } else if(SpoutUtils.isOrder(message)){
                LOG.info("Emitting new order email: " + message.get("order_id").toString());
                _collector.emit("order", new Values(message.get("email"), message.get("order_id")));
            }

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String sentence(String input) {
        return input;
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

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("forgottenPassword", new Fields("email"));
        declarer.declareStream("newUser", new Fields("newUserEmail"));
        declarer.declareStream("order", new Fields("email", "orderId"));
    }


}
