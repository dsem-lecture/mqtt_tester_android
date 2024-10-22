package dsem.mqtt4j.client;

import android.util.Log;

import java.io.*;
import java.net.*;
import dsem.mqtt4j.global.*;

public class BrokerConnector {
	public Connection conn;
	public String broker_ip;
	public int broker_port;

	public BrokerConnector() {
		this(GlobalConfig.default_broker_ip, GlobalConfig.default_broker_port);
	}
	
	public BrokerConnector(String ip, int port) {
		this.conn = new Connection();
		this.broker_ip = ip;
		this.broker_port = port;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
	public boolean connectBroker() {
		if (this.conn.getSocket() == null || this.conn.getSocket().isClosed()) {
			if (this.conn.connect(broker_ip, broker_port)) {
//				System.out.println("BrokerConnector> MQTTBroker connection is success.");
				Log.d("mqtt4j","BrokerConnector> MQTTBroker connection is success.");
				return true;
			}
		}

//		System.out.println("BrokerConnector> MQTTBroker connection is failed.");
		Log.e("mqtt4j","BrokerConnector> MQTTBroker connection is failed.");
		return false;
	}

	public boolean disconnectBroker() {
		if (this.conn.getSocket() != null) {
			if (this.conn.disconnect()) {
//				System.out.println("BrokerConnector> MQTTBroker is connected successfully.");
				Log.d("mqtt4j","BrokerConnector> MQTTBroker disconnected successfully.");
				return true;
			}
		}

//		System.out.println("BrokerConnector> MQTTBroker disconnection is failed.");
		Log.d("mqtt4j","BrokerConnector> MQTTBroker disconnection is failed.");
		return false;
	}

	public boolean registerPublisher() {
		Message msg = new Message(Protocol.TOPIC_REGISTER_PUBLISHER, "Publisher registration");
		
		String jsonMsg = JSONManager.createJSONMessage(msg);
		
		if (conn.sendMessage(jsonMsg)) {
//			System.out.println("BrokerConnector> publish (topic: " + msg.topic + ") : " + msg.message);
			Log.d("mqtt4j","BrokerConnector> publish (topic: " + msg.topic + ") : " + msg.message);
			return true;
		}

		return false;
	}
	
	public boolean publishMessage(String topic, String message) {
		Message msg = new Message(topic, message);
		String jsonMsg = JSONManager.createJSONMessage(msg);
		
		if (conn.sendMessage(jsonMsg)) {
//			System.out.println("BrokerConnector> publish (topic: " + topic + ") : " + message);
			Log.d("mqtt4j","BrokerConnector> publish (topic: " + topic + ") : " + message);
			return true;
		}
		
		return false;
	}

	public boolean joinSubscriber(String topic) {
		Message msg = new Message(Protocol.TOPIC_JOIN_SUBSCRIBER, topic);
				
		String jsonMsg = JSONManager.createJSONMessage(msg);
		
		if (conn.sendMessage(jsonMsg)) {
//			System.out.println("BrokerConnector> publish (topic: " + msg.topic + ") : " + msg.message);
			Log.d("mqtt4j","BrokerConnector> publish (topic: " + msg.topic + ") : " + msg.message);
			return true;
		}
		
		return false;
	}
	
	public String subscirbe() {
		String recvMsg = conn.receiveMessage();
		if (recvMsg == null) 
			return null;
		
		Message subMsg = JSONManager.parseMessage(recvMsg);
		if (subMsg == null)
			return null;
		
//		System.out.println("BrokerConnector> subscribe (topic: " + subMsg.topic + ") : " + subMsg.message);
		Log.d("mqtt4j","BrokerConnector> subscribe (topic: " + subMsg.topic + ") : " + subMsg.message);

		return subMsg.message; 
	}
}
