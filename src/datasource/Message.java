package datasource;

import java.util.Map;

import com.google.gson.JsonElement;

public class Message {
	private String id;
	private Map<String, JsonElement> data;
//	private Map<String, Object> data;
	
	public static final String ID_MESSAGE_GAME_START	= "server.game.start";
	public static final String ID_MESSAGE_GAME_CARDS	= "server.game.cards";
	public static final String ID_MESSAGE_BOARD_CARDS	= "server.game.board.cards";
	public static final String ID_MESSAGE_HAND_START 	= "game.hand.start";
	public static final String ID_MESSAGE_HAND_END	 	= "game.hand.end";
	public static final String ID_MESSAGE_PLAY		 	= "server.game.play";
	public static final String ID_MESSAGE_FAILURE 		= "server.game.play.failure";
	public static final String ID_MESSAGE_SUCCESS 		= "server.game.play.success";
	
	public static final String DATA_KEY_CARDS			= "cards";
	public static final String DATA_KEY_PLAYERS			= "players";
	public static final String DATA_KEY_INFO_PLAYER		= "info";
	public static final String DATA_KEY_COUNT			= "count";
	public static final String DATA_KEY_PLAYER_ACTION	= "id";
	public static final String DATA_KEY_ACTION			= "action";
	public static final String DATA_KEY_HAND_WINNERS	= "winners";
	public static final String DATA_KEY_END_WINNER		= "winner";
	
	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, JsonElement> getData() {
		return data;
	}

	public void setData(Map<String, JsonElement> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Message [id = " + id + ", data = " + data + "]";
	}
	
	private boolean isReachableData(String data_type)
	{
		return this.data.keySet().contains(data_type);
	}

	public Object getRawData(String data_type) {
		if (isReachableData(data_type))
			return data.get(data_type);
		return "";
	}
}
