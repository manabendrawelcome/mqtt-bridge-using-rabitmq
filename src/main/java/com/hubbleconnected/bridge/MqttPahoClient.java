package com.hubbleconnected.bridge;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class MqttPahoClient implements MqttCallback {
	
	final static Logger logger = Logger.getLogger(MqttPahoClient.class);
	MqttClient mqttClient;
	RabitMqClient rabitMqClient;
	private static ConcurrentHashMap<String, String> mapMessages = new ConcurrentHashMap<String, String>();
	Utils utils;
	
	public MqttPahoClient() throws MqttException {
		utils = new Utils();
		mqttClient = new MqttClient(utils.getProperty("mqtt_server_url"), utils.getProperty("paho_client_id"));
		mqttClient.connect();
		mqttClient.setCallback(this);
	}

	public void start() {
		try {
			mqttClient.subscribe(utils.getProperty("topic_name"), Integer.parseInt(utils.getProperty("qos")));
			logger.info("MQTT Paho Client Started...");
		} catch (MqttException e) {
			logger.error(e);
		}
	}

	public void sendMessage(String topic, String msg)
			throws MqttPersistenceException, MqttException {
		MqttMessage message = new MqttMessage();
		message.setPayload(msg.getBytes());
		message.setQos(Integer.parseInt(utils.getProperty("qos")));
		mqttClient.publish(topic, message);
		mapMessages.put(topic.concat(utils.getProperty("delimiter")).concat(msg), "");
	}

	public void connectionLost(Throwable cause) {
		logger.error("connectionLost...");
	}

	public void messageArrived(String topic, MqttMessage message)
			throws Exception {
		String temp_msg = mapMessages.get(topic.concat(utils.getProperty("delimiter")).concat(message.toString()));
		if (temp_msg != null) {
			mapMessages.remove(topic.concat(utils.getProperty("delimiter")).concat(message.toString()));
		} else {
			rabitMqClient.sendMessage(topic, message.toString());
		}
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
	}

	public void setRabitMqClient(RabitMqClient rabitMqClient) {
		this.rabitMqClient = rabitMqClient;
	}

}
