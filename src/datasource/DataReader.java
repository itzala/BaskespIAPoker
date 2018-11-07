package datasource;

import card.Card;
import player.ActionPlayer;
import player.Player;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
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
	
	public ArrayList<Card> getCards()
	{
		Type listType = new TypeToken<List<Card>>() {}.getType();
		ArrayList<Card> cards = parser.fromJson(message.getRawData(Message.DATA_KEY_CARDS).toString(), listType);
		
		return cards;
	}
	
	public Player[] getPlayers()
	{
		Player[] players = parser.fromJson(message.getRawData(Message.DATA_KEY_PLAYERS).toString(), Player[].class);
		
		return players;
	}
	
	public Player getPlayer()
	{
		Player current_player = parser.fromJson(message.getRawData(Message.DATA_KEY_INFO_PLAYER).toString(), Player.class);
		if (current_player == null){
			System.out.println("[WARNING] Pas de joueur accessible !");
		}		
		return current_player;
	}

	public Player[] getWinners() {
		Player[] players = parser.fromJson(message.getRawData(Message.DATA_KEY_HAND_WINNERS).toString(), Player[].class);
		
		return players;	
	}
	
	public Player getFinalWinner()
	{
		Player winner = parser.fromJson(message.getRawData(Message.DATA_KEY_END_WINNER).toString(), Player.class);
		return winner;
	}

	public String getReasonOfFailJoinLobby() {
		String reason = parser.fromJson(message.getRawData(Message.DATA_KEY_REASON).toString(), String.class);
		return reason;
	}

	public int getIdPlayerAction() {
		int id_player = parser.fromJson(message.getRawData(Message.DATA_KEY_PLAYER_ACTION).toString(), Integer.class);
		return id_player;
	}

	public int getNbRivals() {
		int nb_rivals = parser.fromJson(message.getRawData(Message.DATA_KEY_COUNT).toString(), Integer.class);
		return nb_rivals - 1;
	}

	public int getBetValue() {
		int id_player = parser.fromJson(message.getRawData(Message.DATA_KEY_VALUE).toString(), Integer.class);
		return id_player;
	}
	
	public HashMap<String, Integer> getBlindAmount()
	{
		HashMap<String, Integer> blinds = new HashMap<String, Integer>();
		
		blinds.put(Message.DATA_KEY_BIG_BLIND, parser.fromJson(message.getRawData(Message.DATA_KEY_BIG_BLIND).toString(), Integer.class));
		blinds.put(Message.DATA_KEY_SMALL_BLIND, parser.fromJson(message.getRawData(Message.DATA_KEY_SMALL_BLIND).toString(), Integer.class));
		
		return blinds;
	}
}