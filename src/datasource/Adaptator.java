package datasource;

import java.io.FileNotFoundException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


import card.Card;
import game.Game;
import player.ActionPlayer;
import player.Player;

public class Adaptator {
	
	private Game game;
	private final String DATAFILE = "hand.json";
	private JsonReader reader;
	private Gson gson;
	
	public Adaptator(Game game)
	{
		this.game = game;
		gson = new Gson();
		try {
			reader = new JsonReader(new FileReader(System.getProperty("user.dir") + "/data/" + DATAFILE));			
			
		} catch (FileNotFoundException e) {
			reader = null;
			e.printStackTrace();
		}
	}
	
	public void parseData()
	{
		if (reader != null)
		{
			
			Message[] messages = gson.fromJson(reader, Message[].class);
			DataReader data_reader = new DataReader();
			for (Message m : messages) {
				data_reader.setMessage(m);
				System.out.println("----------------------------------------------");
				switch (m.getId()) {
					case Message.ID_MESSAGE_GAME_CARDS:
					case Message.ID_MESSAGE_BOARD_CARDS:
						ArrayList<Card> cards = data_reader.getCards();
						System.out.println("##############################################");
						System.out.println("Nouvelles cartes => "+ cards.toString());
						System.out.println("##############################################");
						game.addNewCards(cards);
						System.out.println("Résultat pour la main => ");
						System.out.println(game.getHand());
						break;
					case Message.ID_MESSAGE_GAME_START:
						Player p = data_reader.getPlayer();
						game.startGame(p);
						System.out.println("Info player : " + game.getPlayer());
						break;
					case Message.ID_MESSAGE_HAND_START:
						System.out.println("Début d'une nouvelle main !!");
						game.startHand();
						break;
					case Message.ID_MESSAGE_HAND_END:
						ArrayList<Player> winners = data_reader.getWinners();
						game.finishHand(winners);
						break;
					case Message.ID_MESSAGE_PLAY:
//						ActionPlayer action = game.doAction();
						
						break;
					case Message.ID_MESSAGE_SUCCESS:
						System.out.println("Coup valide donc pris en compte !");
						break;
					case Message.ID_MESSAGE_FAILURE:
						System.out.println("Coup invalide... Retentez votre coup");
						break;
					default:
						System.out.println("Message '" + m.getId() + "' non géré...");
						System.out.println(m.getData());
						break;
				}
				System.out.println("----------------------------------------------");
			}
		}
		
	}

	public void release()
	{
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
