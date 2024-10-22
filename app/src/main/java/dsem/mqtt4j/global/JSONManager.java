package dsem.mqtt4j.global;

import android.util.Log;

import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.*;

public class JSONManager {
	public static Message parseMessage(String jsonMessage) {
		if (jsonMessage == null)
			return null;
		
		JSONParser parser = new JSONParser();
		JSONObject obj;
		
		try {
			obj = (JSONObject) parser.parse(jsonMessage);
			String topic = (String) obj.get("topic");
			String message = (String) obj.get("message");

			return new Message(topic, message);
		}catch (Exception e) {
//			System.out.println("Exception occurred> JSONManager.parseMessage()");
//			System.out.println(e.getMessage());
//			e.printStackTrace();
			Log.e("mqtt4j","Exception occurred> JSONManager.parseMessage()");
		}
		
		return null;
	}
	
	public static String createJSONMessage(Message msg) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("topic", msg.topic);
			obj.put("message", msg.message);
		} catch (Exception e) {
//			System.out.println("Exception occurred> JSONManager.createJSONMessage()");
//			System.out.println(e.getMessage());
//			e.printStackTrace();
			Log.e("mqtt4j","Exception occurred> JSONManager.createJSONMessage()");

		}
		
		return obj.toString();
	}

	public static Message receiveJSONMessage(BufferedReader reader) {
		StringBuffer jsonMsg = new StringBuffer();
		String line;
		
		try {
			while ((line = reader.readLine()) != null) {
				jsonMsg.append(line);
	        }
			
			return parseMessage(jsonMsg.toString());
		} catch (Exception e) {
//			System.out.println("Exception occurred> JSONManager.receiveJSONMessage()");
//			System.out.println(e.getMessage());
//			e.printStackTrace();
			Log.e("mqtt4j","Exception occurred> JSONManager.receiveJSONMessage()");

		}
		
		return null;
	}	
	
}
