package com.turbointernational.caretaker.customer.storm;


import com.rabbitmq.client.*;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
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
    private static final String QUEUE_NAME = "test_java";
    SpoutOutputCollector _collector;
    private final String rabbitHost = System.getProperty("rabbitHost");
    Random _rand;
    private static Channel channel;

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
        _rand = new Random();
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(rabbitHost);
        Connection connection = null;
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextTuple() {
        try {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                LOG.info("Emitting forgotten email: " +  message);
                _collector.emit("forgottenPassword",new Values(message));
            }
        };
        try {
            channel.basicConsume(QUEUE_NAME, true, consumer);
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
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("forgottenPassword", new Fields("email"));
        declarer.declareStream("newUser", new Fields("newUserEmail"));
    }

}
