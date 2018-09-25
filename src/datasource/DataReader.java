package datasource;

import card.Card;
import player.Player;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataReader {
	private Gson parser;
	private Message message;
	
	public DataReader()
	{
		parser = new Gson();
	}
	
	public void setMessage(Message m)
	{
		message = m;
	}
	
	public Message getMessage()
	{
		return message;
	}
	
	public Gson getParser() {
		return parser;
	}

	public void setParser(Gson parser) {
		this.parser = parser;
	}
	
	public ArrayList<Card> getCards()
	{
		Type listType = new TypeToken<List<Card>>() {}.getType();
		ArrayList<Card> cards = parser.fromJson(message.getRawData(Message.DATA_KEY_CARDS).toString(), listType);
		
		return cards;
	}
	
	public ArrayList<Player> getPlayers()
	{
		Type listType = new TypeToken<List<Player>>() {}.getType();
		ArrayList<Player> players = parser.fromJson(message.getRawData(Message.DATA_KEY_PLAYERS).toString(), listType);
		
		return players;
	}
	
	public Player getPlayer()
	{
		Player current_player = parser.fromJson(message.getRawData(Message.DATA_KEY_INFO_PLAYER).toString(), Player.class);
		if (current_player == null)
			System.out.println("[WARNING] Pas de joueur accessible !");
		
		return current_player;
	}

	public ArrayList<Player> getWinners() {
		Type listType = new TypeToken<List<Player>>() {}.getType();
		ArrayList<Player> players = parser.fromJson(message.getRawData(Message.DATA_KEY_HAND_WINNERS).toString(), listType);
		
		return players;	}
	
}