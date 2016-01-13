package com.hubbleconnected.bridge;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

public class StartPoint {
	final static Logger logger = Logger.getLogger(StartPoint.class);
	
	public void start(){
		try {
			MqttPahoClient mqttPahoClient = new MqttPahoClient();
			RabitMqClient rabitMqClient = new RabitMqClient();
			mqttPahoClient.setRabitMqClient(rabitMqClient);
			rabitMqClient.setMqttPahoClient(mqttPahoClient);
			mqttPahoClient.start();
			rabitMqClient.start();
			
		} catch (MqttException e) {
			logger.error(e);
		}
	}

	public static void main(String[] args) {
		StartPoint s = new StartPoint();
		s.start();
	}
}
