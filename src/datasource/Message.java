package datasource;

import java.util.Map;

import com.google.gson.JsonElement;

public class Message {
	private String id;
	private Map<String, JsonElement> data;
	
	public static final String ID_MESSAGE_GAME_START	= "server.game.start";
	public static final String ID_MESSAGE_GAME_CARDS	= "server.game.player.cards";
	public static final String ID_MESSAGE_BOARD_CARDS	= "server.game.board.cards";
	public static final String ID_MESSAGE_LOBBY_SUCCESS	= "server.lobby.join.success";
	public static final String ID_MESSAGE_LOBBY_FAILURE	= "server.lobby.join.failure";
	public static final String ID_MESSAGE_PLAY		 	= "server.game.player.play";
	public static final String ID_MESSAGE_FAILURE 		= "server.game.play.failure";
	public static final String ID_MESSAGE_SUCCESS 		= "server.game.play.success";
	public static final String ID_MESSAGE_GAME_END 		= "server.game.end";
	public static final String ID_MESSAGE_PLAYER_ACTION	= "server.game.player.action";
	public static final String ID_MESSAGE_CLIENT_ACTION	= "client.game.player.play";
	public static final String ID_MESSAGE_JOIN_LOBBY	= "client.lobby.join";
	public static final String ID_MESSAGE_PLAY_TIMEOUT	= "server.game.play.timeout";
	public static final String ID_MESSAGE_HAND_START 	= "server.game.hand.start";
	public static final String ID_MESSAGE_HAND_END	 	= "server.game.hand.end";
	public static final String ID_MESSAGE_CHANGE_BLIND	= "server.game.blind.change";
	public static final String ID_MESSAGE_TURN_START	= "server.game.turn.start";
	public static final String ID_MESSAGE_TURN_END		= "server.game.turn.end";
	
	
	public static final String DATA_KEY_CARDS			= "cards";
	public static final String DATA_KEY_PLAYERS			= "players";
	public static final String DATA_KEY_INFO_PLAYER		= "info";
	public static final String DATA_KEY_COUNT			= "count";
	public static final String DATA_KEY_PLAYER_ACTION	= "id";
	public static final String DATA_KEY_ACTION			= "action";
	public static final String DATA_KEY_HAND_WINNERS	= "winners";
	public static final String DATA_KEY_END_WINNER		= "winner";
	public static final String DATA_KEY_NAME_TEAM		= "name";
	public static final String DATA_KEY_REASON 			= "reason";
	public static final String DATA_KEY_VALUE 			= "value";
	public static final String DATA_KEY_BIG_BLIND		= "big";
	public static final String DATA_KEY_SMALL_BLIND		= "small";
	
		
	
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
