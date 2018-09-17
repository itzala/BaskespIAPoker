package datasource;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Message {
	private String id;
	private Map<String, ArrayList<Map<String, String>>> data;
	public static String ID_MESSAGE_CARDS="server.game.cards";

	public Message(Map<String, ArrayList<Map<String, String>>> data)
	{
		this.data = data;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, ArrayList<Map<String, String>>> getData() {
		return data;
	}

	public void setData(Map<String, ArrayList<Map<String, String>>> data) {
		this.data = data;
	}
	
	public String getRawData()
	{
		return data.toString();
	}
	
	public Set<String> getDifferentPartsOfMessage()
	{
		return data.keySet();
	}
	
	@Override
	public String toString() {
		return "Message [id = " + id + ", data = " + data + "]";
	}
}
