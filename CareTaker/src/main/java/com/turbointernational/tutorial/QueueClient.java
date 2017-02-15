package com.turbointernational.tutorial;
import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.nio.NioParams;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by kshakirov on 2/3/17.
 */
public class QueueClient {
    private final static String QUEUE_NAME = "customer_email";

    public static void main(String[] argv)
            throws java.io.IOException,
            java.lang.InterruptedException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.useNio();
        factory.setNioParams(new NioParams().setNbIoThreads(4));
        factory.setHost("localhost");
        Connection connection = null;
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        try {
            Channel                    channel = connection.createChannel();
            channel.queueDeclareNoWait(QUEUE_NAME, false, false, false, null);
            channel.basicQos(1);
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(QUEUE_NAME, false, consumer);
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
            //channel.close();
            //connection.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
