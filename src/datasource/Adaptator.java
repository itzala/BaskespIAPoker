package datasource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import card.Card;
import constantes.Constantes;
import game.Game;
import player.ActionPlayer;
import player.Player;

public class Adaptator {
	
	private Game game;
	private Socket client_socket;
	private BufferedReader reader	= null;
	private PrintWriter writer;
	private Gson gson;
	
	public Adaptator(Game game)
	{
		this.game = game;
		gson = new Gson();
	}
	
	public void connect()
	{
		try {
			System.out.println("Connexion au serveur : " + Constantes.SERVER_ADDRESS + ":"  + Constantes.SERVER_PORT);
			client_socket = new Socket(Constantes.SERVER_ADDRESS, Constantes.SERVER_PORT);
			reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client_socket.getOutputStream())), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void listen()
	{
		if (reader != null)
		{
			try {
				String received_data = "";
				
				while ((received_data = reader.readLine()) != null) 
				{
					System.out.println(received_data);
					
					Message m = gson.fromJson(received_data, Message.class);
					DataReader data_reader = new DataReader();
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendResponse(String id_message, JsonElement data_message)
	{
		Message m = new Message();
		m.setId(id_message);
		Map<String, JsonElement> data = new HashMap<String, JsonElement>();
		switch (id_message){
			case Message.ID_MESSAGE_CLIENT_ACTION:
				data.put(Message.DATA_KEY_ACTION, data_message);
				break;
			case Message.ID_MESSAGE_JOIN_LOBBY:
				data.put(Message.DATA_KEY_NAME_TEAM, data_message);
				break;
		}
		m.setData(data);
		writer.println(gson.toJson(m));
	}

	public void release()
	{
		try {
			reader.close();
			client_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
