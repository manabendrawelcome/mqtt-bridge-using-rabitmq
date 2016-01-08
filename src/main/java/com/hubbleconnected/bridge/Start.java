package com.hubbleconnected.bridge;

import org.eclipse.paho.client.mqttv3.MqttException;

public class Start {

	public static void main(String[] args) {
		
		try {
			MqttPahoClient mqttPahoClient = new MqttPahoClient();
			RabitMqClient rabitMqClient = new RabitMqClient();
			mqttPahoClient.setRabitMqClient(rabitMqClient);
			rabitMqClient.setMqttPahoClient(mqttPahoClient);
			mqttPahoClient.start();
			rabitMqClient.start();
			
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

}
