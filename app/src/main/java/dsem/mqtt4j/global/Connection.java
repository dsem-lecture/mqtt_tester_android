package dsem.mqtt4j.global;

import android.util.Log;

import java.io.*;
import java.net.*;

public class Connection {
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	
	public Connection() {
		this.socket = null;
		this.reader = null;
		this.writer = null;
	}
	
	public Connection(Socket socket) {
		this.socket = socket;
		try {
			this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
		} catch (Exception e) {
//			System.out.println("Exception occurred> dsem.mqtt4j.global.Connection.Connection()");
//			System.out.println(e.getMessage());
//			e.printStackTrace();
			Log.e("mqtt4j","Exception occurred> dsem.mqtt4j.global.Connection.Connection()");
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public PrintWriter getWriter() {
		return writer;
	}
	
	public void setConnection(Socket socket) {
		this.socket = socket;
		try {
			this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
		} catch (Exception e) {
//			System.out.println("Exception occurred> dsem.mqtt4j.global.Connection.setConnection(Socket socket)");
//			System.out.println(e.getMessage());
//			e.printStackTrace();
			Log.e("mqtt4j","Exception occurred> dsem.mqtt4j.global.Connection.setConnection(Socket socket)");
		}
	}

	public boolean connect(String ip, int port) {
		try {
			Log.d("mqtt4j", "ip : " + ip + " port : " + port);
			Socket socket = new Socket(ip, port);
	        this.setConnection(socket);
		} catch (Exception e) {
//			System.out.println("Exception occurred> dsem.mqtt4j.global.Connection.connect()");
//			System.out.println(e.getMessage());
//			e.printStackTrace();
			Log.e("mqtt4j","Exception occurred> dsem.mqtt4j.global.Connection.connect()");

			return false;
		}
		
		return true;
	}
	
	public boolean disconnect() {
		try {
			this.reader.close();
			this.writer.close();
			this.socket.close();
		} catch (Exception e) {
//			System.out.println("Exception occurred> dsem.mqtt4j.global.Connection.disconnect()");
//			System.out.println(e.getMessage());
//			e.printStackTrace();
			Log.e("mqtt4j","Exception occurred> dsem.mqtt4j.global.Connection.disconnect()");

			return false;
		}
		
		return true;
	}

	public boolean testConnection() {
		try {
			this.writer.println(Protocol.CONNECTED);
			this.writer.flush();

			String line = this.reader.readLine();
			if(Protocol.ACK.equals(line)) {
				return true;
			}
		} catch (Exception e) {
//			System.out.println("Exception occurred> disconnected");
//			System.out.println(e.getMessage());
//			e.printStackTrace();
			Log.e("mqtt4j","Exception occurred> disconnected");

		}
		
		return false;
	}
	
	public void sendAck() {
		this.writer.println(Protocol.ACK);
		this.writer.flush();
//		System.out.println("Connection> send Acknowledgement protocol : " + Protocol.ACK);
	}
	
	public boolean sendMessage(String message) {
		try {
//			System.out.println("Connection> sendMessage : " + message);
			this.writer.println(message);
			this.writer.println(Protocol.MESSAGE_END);
			this.writer.flush();
		}catch (Exception e) {
//			System.out.println("Exception occurred> dsem.mqtt4j.global.Connection.sendMessage()");
//			System.out.println(e.getMessage());
//			e.printStackTrace();
			Log.e("mqtt4j","Exception occurred> dsem.mqtt4j.global.Connection.sendMessage()");

			return false;
		}
		
		return true;
	}
	
	public String receiveMessage() {
		StringBuilder sb = new StringBuilder();
		String line;
		String message = "";

		try {
			while ((line = this.reader.readLine()) != null) {
				if (Protocol.MESSAGE_END.equals(line)) {
					break;
				} else if(Protocol.CONNECTED.equals(line)) {
					sendAck();					
					continue;
				}
				
				sb.append(line);
	        }
			
			message = sb.toString();
//			System.out.println("Connection> receiveMessage : " + message);
		} catch (Exception e) {
//			System.out.println("Exception occurred> dsem.mqtt4j.global.Connection.receiveMessage()");
//			System.out.println(e.getMessage());
//			e.printStackTrace();
			Log.e("mqtt4j","Exception occurred> dsem.mqtt4j.global.Connection.receiveMessage()");

			this.disconnect();
			
			return null;
		}
		
		return message;
	}
}
