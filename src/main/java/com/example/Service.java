package com.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

class Service {

  private static final String RAPID = "rapid";

  /* Proper serialization requires services to share data definition. :/
  private static byte[] serialize(Object o) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(o);
      return baos.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static Object deserialize(byte[] input) {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(input);
      ObjectInputStream in = new ObjectInputStream(bais);
      return in.readObject();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  */

  private static void send(String event, String body) throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException, IOException, TimeoutException {
    Channel channel = Service.getChannel();
    Service.publish(channel, event, body);
    channel.close();
  }

  private static void publish(Channel channel, String event, String body) {
    try {
      // System.out.println("Sending '" + event + "' event");
      channel.basicPublish(Service.RAPID, event, null, body.getBytes("UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Channel getChannel() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {
    String uri = System.getenv("CLOUDAMQP_URL");
    if (uri == null) {
      uri = "amqp://guest:guest@localhost";
    }
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri(uri);
    // factory.setRequestedHeartbeat(30);
    // factory.setConnectionTimeout(30);
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.exchangeDeclare(Service.RAPID, "direct", false);
    return channel;
  }

  static Subscription.Sender service(Subscription[] subs) throws
      NoSuchAlgorithmException, KeyManagementException, URISyntaxException, IOException, TimeoutException {
    Channel channel = Service.getChannel();

    for (Subscription sub : subs) {
      channel.queueDeclare(sub.river, true, false, false, null);
      channel.queueBind(sub.river, Service.RAPID, sub.event);

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        if (!delivery.getEnvelope().getRoutingKey().equals(sub.event)) {
          channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
          return;
        }
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

        String body = new String(delivery.getBody(), "UTF-8");
        sub.handle.apply(body, (event, body1) -> {
          Service.publish(channel, event, body1);
        });
      };
      channel.basicConsume(sub.river, false, deliverCallback, consumerTag -> {
      });
    }
    return (event, body1) -> {
      Service.publish(channel, event, body1);
    };
  }

}
