package datasource;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import card.Card;
import game.Game;

public class Adaptator {
	
	private Game game;
	private final String DATAFILE = "hand.json";
	private JsonReader reader;
	
	public Adaptator(Game game)
	{
		this.game = game;
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
			Gson gson = new Gson();
			Message m = gson.fromJson(reader, Message.class);
			game.startGame(3, "Baskesp");
			System.out.println(m);			
			ArrayList<Map<String, String>> json_cards = m.getData().get("cards");
			DataReader data_reader = new DataReader();
			data_reader.setData(json_cards);
			System.out.println("##############################################");
			System.out.println("Get 'CARDS' => "+ json_cards);
			System.out.println("##############################################");
			ArrayList<Card> cards = data_reader.getCards();
			game.addNewCards(cards);
			System.out.println("Résultat pour la main => ");
			System.out.println(game.getHand());
			
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
