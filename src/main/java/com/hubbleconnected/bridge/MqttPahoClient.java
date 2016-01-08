package com.hubbleconnected.bridge;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class MqttPahoClient implements MqttCallback {
	
	private final static String TOPIC_NAME = "myTopic";
	private final static String MQTT_SERVER_URL = "tcp://54.209.195.112:1883";
	MqttClient client;
	RabitMqClient rabitMqClient;

	public MqttPahoClient() throws MqttException {
		client = new MqttClient(MQTT_SERVER_URL, "MqttPahoClient");
		client.connect();
		client.setCallback(this);
	}

	public void start() {
		try {
			System.out.println("--------mqtt client started-----------");
			
			client.subscribe(TOPIC_NAME);

		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String msg) throws MqttPersistenceException,
			MqttException {
		MqttMessage message = new MqttMessage();
		message.setPayload(msg.getBytes());
		client.publish(TOPIC_NAME, message);
	}

	// @Override
	public void connectionLost(Throwable cause) {
		System.out.println("connectionLost...");
		System.out.println(cause);

	}

	// @Override
	public void messageArrived(String topic, MqttMessage message)
			throws Exception {
		System.out.println("Message published from Mqtt:: " + message);
		rabitMqClient.sendMessage(message.toString());
	}

	// @Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("deliveryComplete...");

	}

	public void setRabitMqClient(RabitMqClient rabitMqClient) {
		this.rabitMqClient = rabitMqClient;
	}

}
