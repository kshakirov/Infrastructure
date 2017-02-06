package com.turbointernational.tutorial;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by kshakirov on 2/3/17.
 */
public class QueueClient {
    private final static String QUEUE_NAME = "test_java";

    public static void main(String[] argv)
            throws java.io.IOException,
            java.lang.InterruptedException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        final String[] message = new String[1];

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                message[0] = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message[0] + "'");
            }
        };
        String response = channel.basicConsume(QUEUE_NAME, true, consumer);
        System.out.println(message[0]);

    }
}
