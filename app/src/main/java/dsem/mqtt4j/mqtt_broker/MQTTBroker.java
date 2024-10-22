package dsem.mqtt4j.mqtt_broker;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import dsem.mqtt4j.global.*;

public class MQTTBroker {
	private int port;
	static HashMap<String, ArrayList<Connection>> subConnMap;

	public MQTTBroker() {		
		port = GlobalConfig.default_broker_port;
		subConnMap = new HashMap<String, ArrayList<Connection>>();
	}

	public void startBroker() {
		try (ServerSocket serverSocket = new ServerSocket(this.port)) {
			System.out.println("MQTTBroker> MQTT Broker starts.");

			while (true) {
				System.out.println("MQTTBroker> Waiting for clients....");
				Socket socket = serverSocket.accept();
				System.out.println("MQTTBroker> Connection Requested.");

				Connection clientConn = new Connection(socket);
				
				ClientManager cm = new ClientManager(clientConn);
				cm.start();
				System.out.println("MQTTBroker> Client Manager starts.");
			}
		} catch (Exception e) {
			System.out.println("Exception occurred> dsem.mqtt4j.mqtt_broker.MQTTBroker.startBroker()");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new MQTTBroker().startBroker();
	}
}
