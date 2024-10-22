package dsem.mqtt4j.mqtt_broker;

import java.io.*;
import java.util.*;
import dsem.mqtt4j.global.*;

class PublishListener extends Thread {
	private Connection conn;

	public PublishListener(Connection conn) {
		super();
		this.conn = conn;
	}

	public void checkConnections(ArrayList<Connection> connlist) {
		for (int i=0; i<connlist.size(); i++) {
			Connection connection = connlist.get(i);
			if (connection.testConnection()) {
				System.out.println("PublishListener> connection check Client #" + i + " : " + "success");
			} else {
				System.out.println("PublishListener> connection check Client #" + i + " : " + "failure");
				connection.disconnect();
				System.out.println("PublishListener> Client #" + i + " : " + "removed");
				connlist.remove(i);
				i--;
			}
		}		
	}
	
	public synchronized void publishMessage(Message message) {
		if (message == null) return;
		
		ArrayList<Connection> connlist = MQTTBroker.subConnMap.get(message.topic);

		if (connlist == null) {
			System.out.println("PublishListener> there is no subscriber(topic : " + message.topic + ")");
			return;
		} else {
			checkConnections(connlist);
		}
		
		String sendMessage = JSONManager.createJSONMessage(message);
		System.out.println("PublishListener> Publish message : " + sendMessage);
		if (sendMessage == null) return;
		
		for (int i=0; i<connlist.size(); i++) {
			Connection connection = connlist.get(i);
			try {
				connection.sendMessage(sendMessage);
				System.out.println("PublishListener> Client #" + i + " > publish success");
			} catch (Exception e) {
				connection.disconnect();
				connlist.remove(i);
				System.out.println("Exception occured> dsem.mqtt4j.mqtt_broker.PublishListener.publishMessage()");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}		

	}
	
	public void run() {
		try {
			while(true) {
				String recvMessage = this.conn.receiveMessage();
				System.out.println("PublishListener> publish message received : " + recvMessage);
				Message message = JSONManager.parseMessage(recvMessage);
				if (message==null && !this.conn.testConnection()) {
					System.out.println("PublishListener> publisher is disconnected");
					break;
				}
				
				publishMessage(message);
			}
		} catch (Exception e) {
			System.out.println("Exception occured> dsem.mqtt4j.mqtt_broker.PublishListener.run()");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}