package com.hubbleconnected.bridge;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class RabitMqClient {
	
	final static Logger logger = Logger.getLogger(RabitMqClient.class);
	Channel channel;
	MqttPahoClient mqttPahoClient;
	Utils utils;
	
	public RabitMqClient() {
		utils = new Utils();
		try {
			ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost(utils.getProperty("rabbitmq_server_ip"));
		    Connection connection;
			connection = factory.newConnection();
			channel = connection.createChannel();
		    channel.queueDeclare(utils.getProperty("queue1"), false, false, false, null);
		    channel.queueDeclare(utils.getProperty("queue2"), false, false, false, null);
			
		} catch (IOException e) {
			logger.error(e);
		} catch (TimeoutException e) {
			logger.error(e);
		}
	}
	
	public void sendMessage(String topic, String message){
		try {
			//corelation id # setting as topic
			BasicProperties prop = new BasicProperties(null,null,null,null,null,topic,null,null,null,null,null,null,null,null);
			channel.basicPublish("", utils.getProperty("queue1"), prop, message.getBytes("UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	public void consumeMessage(){
		Consumer consumer = new DefaultConsumer(channel) {
		      @Override
		      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
		          throws IOException {
		        String message = new String(body, "UTF-8");
		        String topic = properties.getCorrelationId();
		        
		        try {
					mqttPahoClient.sendMessage(topic, message);
				} catch (MqttPersistenceException e) {
					logger.error(e);
				} catch (MqttException e) {
					logger.error(e);
				}
		      }
		    };
		    
		    try {
				channel.basicConsume(utils.getProperty("queue2"), true, consumer);
				
			} catch (IOException e) {
				logger.error(e);
			}
		
	}
	
	public void start() {
		consumeMessage();
		logger.info("RabbitMQ Client Started...");
	}

	public void setMqttPahoClient(MqttPahoClient mqttPahoClient) {
		this.mqttPahoClient = mqttPahoClient;
	}

}
