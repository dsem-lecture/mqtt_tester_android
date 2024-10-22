package dsem.mqtt4j.mqtt_broker;

import java.io.*;
import java.net.*;
import java.util.*;
import dsem.mqtt4j.global.*;

class ClientManager extends Thread {
	Connection conn;

	public ClientManager(Connection conn) {
		super();
		this.conn = conn;
	}

	public void checkConnList(String topic) {
		ArrayList<Connection> connlist = MQTTBroker.subConnMap.get(topic);

		if (connlist == null) {
			System.out.println("ClientManager> there is no subscriber(topic : " + topic + ")");
			return;
		}	
		
		System.out.println("ClientManager> connlist size : " + connlist.size());
	}
	
	@Override
	public void run() {
		try {
			System.out.println("ClientManager> Client is connected.");

			String recvMessage = this.conn.receiveMessage();
			if (recvMessage == null) return;
			
			Message message = JSONManager.parseMessage(recvMessage);
			if (message == null) return;
			
			if (Protocol.TOPIC_JOIN_SUBSCRIBER.equals(message.topic)) {
				System.out.println("ClientManager> topic : " + message.topic);
				System.out.println("ClientManager> message : " + message.message);
				String topic = message.message;

				if (MQTTBroker.subConnMap.containsKey(topic)) {
					System.out.println("ClientManager> topic is contained");
					
					ArrayList<Connection> connList = MQTTBroker.subConnMap.get(topic);
					connList.add(this.conn);

					MQTTBroker.subConnMap.put(topic, connList);
					
					System.out.println("ClientManager> New subscriber joined (topic : " + topic + ")");
				} else {
					System.out.println("ClientManager> topic is not contained");
					ArrayList<Connection> connList = new ArrayList<Connection>();
					connList.add(this.conn);
					MQTTBroker.subConnMap.put(topic, connList);

					System.out.println("ClientManager> New subscriber joined and new topic registered (topic : " + topic + ")");
				}
				
				checkConnList(topic);

			} else if (Protocol.TOPIC_REGISTER_PUBLISHER.equals(message.topic)) {
				PublishListener pl = new PublishListener(this.conn);
				pl.start();
			} else {
				System.out.println("ClientManager> topic is not valid.");
			}
		} catch (Exception e) {
			System.out.println("Exception occurred> dsem.mqtt4j.mqtt_broker.SubscriberManager.run()");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}