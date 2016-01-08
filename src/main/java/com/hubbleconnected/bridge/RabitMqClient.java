package com.hubbleconnected.bridge;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabitMqClient {
	
	private final static String QUEUE_NAME = "myQueue";
	private final static String RABITMQ_SERVER_IP = "54.84.122.85";
	Channel channel;
	MqttPahoClient mqttPahoClient;
	
	public RabitMqClient() {
		
		try {
			ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost(RABITMQ_SERVER_IP);
		    Connection connection;
			connection = factory.newConnection();
			channel = connection.createChannel();
		    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String message){
		try {
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
			System.out.println(" [x] Sent '" + message + "'");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	}
	
	public void consumeMessage(){
		Consumer consumer = new DefaultConsumer(channel) {
		      @Override
		      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
		          throws IOException {
		        String message = new String(body, "UTF-8");
		        System.out.println(" [x] Received '" + message + "'");
		        
		        try {
					mqttPahoClient.sendMessage(message);
					
				} catch (MqttPersistenceException e) {
					e.printStackTrace();
				} catch (MqttException e) {
					e.printStackTrace();
				}
		      }
		    };
		    
		    try {
				channel.basicConsume(QUEUE_NAME, true, consumer);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	public void start() {
		System.out.println("--------rabitmq client started-----------");
		consumeMessage();
	}

	public void setMqttPahoClient(MqttPahoClient mqttPahoClient) {
		this.mqttPahoClient = mqttPahoClient;
	}

}
