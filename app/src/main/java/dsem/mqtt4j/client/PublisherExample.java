package dsem.mqtt4j.client;

import dsem.mqtt4j.global.*;

public class PublisherExample {
	public static void main(String[] args) {
		String broker_ip = GlobalConfig.default_broker_ip;
		int broker_port = GlobalConfig.default_broker_port;
		
		BrokerConnector bc = new BrokerConnector(broker_ip, broker_port);
		
		bc.connectBroker();
		bc.registerPublisher();
		
		String topic = "test/publisher1/sensor";
		try {
			while(true) {
				int value = (int)(Math.random()*100);
				
				System.out.println("Publish ("+ topic +") : " + value);
				bc.publishMessage(topic, String.valueOf(value));
				
				Thread.sleep(5000);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("Publisher finished.");
	}
}
