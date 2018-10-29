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
import com.google.gson.JsonPrimitive;

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
			this.log("Connexion au serveur : " + Constantes.SERVER_ADDRESS + ":"  + Constantes.SERVER_PORT);
			client_socket = new Socket(Constantes.SERVER_ADDRESS, Constantes.SERVER_PORT);
			reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client_socket.getOutputStream())), true);
			JsonElement data_message = new JsonPrimitive(Constantes.PLAYER_NAME);
			this.sendResponse(Message.ID_MESSAGE_JOIN_LOBBY, data_message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void log(String message)
	{
		System.out.println("[ CLIENT ] " + message);
	}

	public void listen()
	{
		if (reader != null)
		{
			try {
				String received_data = "";
				
				while ((received_data = reader.readLine()) != null) 
				{
					Message m = gson.fromJson(received_data, Message.class);
					DataReader data_reader = new DataReader();
					data_reader.setMessage(m);
					this.log("----------------------------------------------");
					switch (m.getId()) {
						case Message.ID_MESSAGE_GAME_START :
							Player p = data_reader.getPlayer();
							int nb_rivals = data_reader.getNbRivals();
							game.startGame(p, nb_rivals);
							this.log("Info player : " + game.getPlayer());
							break;
						case Message.ID_MESSAGE_GAME_END :
							Player winner = data_reader.getFinalWinner();
							game.checkWinner(winner);
							break;
						case Message.ID_MESSAGE_GAME_CARDS :
							this.log("Nouvelles cartes pour le joueur");
							ArrayList<Card> player_cards = data_reader.getCards();
							game.addPlayerNewCards(player_cards);
							this.log("Résultat pour la main => ");
							this.log(game.getHand().toString());
							break;
						case Message.ID_MESSAGE_BOARD_CARDS :
							this.log("Nouvelles cartes sur la table....");
							ArrayList<Card> board_cards = data_reader.getCards();
							game.addBoardNewCards(board_cards);
							this.log("Résultat pour la main => ");
							this.log(game.getHand().toString());
							break;
						case Message.ID_MESSAGE_LOBBY_SUCCESS :
							this.log("Lobby rejoint avec success !");
							break;
						case Message.ID_MESSAGE_LOBBY_FAILURE	:
							this.log("Echec lors de la connexion au lobby. Raison évoquée : " + data_reader.getReasonOfFailJoinLobby());
							break;
						case Message.ID_MESSAGE_PLAY :
							ActionPlayer action = game.doAction();
							this.log("A vous de jouer !");
							if (game.isValidAction())
							{
								JsonElement data_message = new JsonPrimitive(action.getValue());
								this.sendResponse(Message.ID_MESSAGE_CLIENT_ACTION, data_message);
							}
							break;
						case Message.ID_MESSAGE_FAILURE :
							this.log("Coup invalide... Retentez votre coup");
							break;
						case Message.ID_MESSAGE_SUCCESS :
							this.log("Coup valide donc pris en compte !");
							game.validateAction();
							break;
						case Message.ID_MESSAGE_PLAYER_ACTION :
							int bet_value = data_reader.getBetValue();
							int id_player = data_reader.getIdPlayerAction();
							game.addBetPlayer(id_player, bet_value);
							break;
						case Message.ID_MESSAGE_HAND_START :
							this.log("Début d'une nouvelle main !!");
							Player[] players_hand = data_reader.getPlayers();
							game.startHand(players_hand);
							break;
						case Message.ID_MESSAGE_HAND_END :
							Player[] winners = data_reader.getWinners();
							game.finishHand(winners);
							break;
						case Message.ID_MESSAGE_PLAY_TIMEOUT :
							game.abortPlayOnTimeout();
							break;
						case Message.ID_MESSAGE_CHANGE_BLIND :
							game.updateBlindAmount(data_reader.getBlindAmount());
							break;
						case Message.ID_MESSAGE_TURN_START:
							this.log("Début d'un nouveau tour de mise");
							break;
						case Message.ID_MESSAGE_TURN_END:
							this.log("Fin du tour de mise");
							break;
						default:
							this.log("Message '" + m.getId() + "' non géré...");
							break;
					}
					this.log("----------------------------------------------");
				}
				this.log("Deconnexion du serveur...");
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
		this.log("Envoi de " + m);
		writer.println(gson.toJson(m));
	}

	public void release()
	{
		try {
			this.log("Fermeture des flux.... avant arrêt du client");
			reader.close();
			client_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
