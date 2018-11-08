package datasource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import card.Card;
import constantes.Constantes;
import game.Game;
import game.StateGame;
import player.ActionPlayer;
import player.Player;

public class Adaptator {
	
	private Game game;
	private Socket clientSocket;
	private PrintWriter writer;
	private InputStream streamFromSocket;
	private Gson gson;
	private boolean isAlive;
	
	public Adaptator(Game game)
	{
		this.game = game;
		GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
		
		isAlive = true;
	}
	
	public void connect()
	{
		try {
			this.log("Connexion au serveur : " + Constantes.SERVER_ADDRESS + " sur le port "  + Constantes.SERVER_PORT);
			clientSocket = new Socket(Constantes.SERVER_ADDRESS, Constantes.SERVER_PORT);
			streamFromSocket = clientSocket.getInputStream();
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
			JsonElement dataMessage = new JsonPrimitive(Constantes.PLAYER_NAME);
			this.sendResponse(Message.ID_MESSAGE_JOIN_LOBBY, dataMessage);
		} catch (ConnectException e2){
			this.log("[ ERROR ] Connexion au serveur impossible... Arrêt du programme");
			isAlive = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String readDataFrom(InputStream stream) throws IOException
	{
		StringBuilder builder = new StringBuilder();
		int bytesReaded;
		byte[] b = new byte[1024];
		
		while(stream.available() > 0 && ((bytesReaded = stream.read(b)) > 0)){
			builder.append(new String(b, 0, bytesReaded));
		}
				
		return builder.toString();
	}
	
	private List<Message> parseReceivedData(String data){
		return parseReceivedData(data, new ArrayList<Message>());
	}
	
	private List<Message> parseReceivedData(String data, List<Message> messages){
		try{
			Message m = gson.fromJson(data, Message.class);
			if (m != null){
				messages.add(m);
			}
		}catch(Exception e)
		{
			/* Permet de gérer le cas où le protocole TCP optimise les envois et que l'on reçoit plusieurs messages en suivant */
			Pattern errorMessagePattern = Pattern.compile("to accept malformed JSON at line 1 column ([0-9]+) path");
			Matcher matcher = errorMessagePattern.matcher(e.getMessage());
			
			if (matcher.find())
			{
				int indexBadCharacter = Integer.parseInt(matcher.group(1))-2;
				Message m = gson.fromJson(data.substring(0, indexBadCharacter), Message.class);
				if (m != null){
					messages.add(m);
				}
				String nextData = data.substring(indexBadCharacter);
				this.parseReceivedData(nextData, messages); // la liste sera mise à jour lors des appels récursifs
			}
		}
		return messages;
	}
	
	private void log(String message){
		System.out.println("[ ADAPTATEUR ] " + message);
	}

	public void listen(){
		if (streamFromSocket != null){
			try {
				while(isAlive){
					String receivedData = readDataFrom(streamFromSocket);
					if (!receivedData.isEmpty()){
						List<Message> receivedMessages = parseReceivedData(receivedData);
						
						for (Message m : receivedMessages) {
							DataReader dataReader = new DataReader();
							dataReader.setMessage(m);
							this.log("----------------------------------------------");
							this.log("Message à traiter : " + m.getId());
							switch (m.getId()) {
								case Message.ID_MESSAGE_GAME_START :
									Player p = dataReader.getPlayer();
									int nb_rivals = dataReader.getNbRivals();
									game.setStateGame(StateGame.GAME_BEGIN);
									game.startGame(p, nb_rivals);
									this.log("Info player : " + game.getPlayer());
									break;
								case Message.ID_MESSAGE_GAME_END :
									Player winner = dataReader.getFinalWinner();
									game.setStateGame(StateGame.GAME_FINISH);
									game.checkWinner(winner);
									isAlive = false;
									break;
								case Message.ID_MESSAGE_GAME_CARDS :
									this.log("Nouvelles cartes pour le joueur");
									ArrayList<Card> playerCards = dataReader.getCards();
									game.addPlayerNewCards(playerCards);
									this.log(game.getHand().toString());
									break;
								case Message.ID_MESSAGE_BOARD_CARDS :
									this.log("Nouvelles cartes sur la table....");
									ArrayList<Card> boardCards = dataReader.getCards();
									game.addBoardNewCards(boardCards);									
									this.log(game.getHand().toString());
									break;
								case Message.ID_MESSAGE_LOBBY_SUCCESS :
									this.log("Lobby rejoint avec success !");
									break;
								case Message.ID_MESSAGE_LOBBY_FAILURE	:
									this.log("Echec lors de la connexion au lobby. Raison évoquée : " + dataReader.getReasonOfFailJoinLobby());
									break;
								case Message.ID_MESSAGE_PLAY :
									ActionPlayer action = game.doAction(-1);
									this.log("A vous de jouer !");
									JsonElement dataMessage = new JsonPrimitive(action.getValue());
									this.sendResponse(Message.ID_MESSAGE_CLIENT_ACTION, dataMessage);
									break;
								case Message.ID_MESSAGE_FAILURE :
									String reason = dataReader.getReasonOfFailurePlay();
									this.log("Coup invalide... " + reason + " Retentez votre coup");
									Pattern errorMessagePattern = Pattern.compile("value less than currentBet : ([0-9]+) vs ([0-9]+)");
									Matcher matcher = errorMessagePattern.matcher(reason);
									
									int minBet = -1;
									
									if (matcher.find()){
										minBet = Integer.parseInt(matcher.group(2));
									}
									ActionPlayer actionRetry = game.doAction(minBet);									
									JsonElement dataMessageRetry = new JsonPrimitive(actionRetry.getValue());
									this.sendResponse(Message.ID_MESSAGE_CLIENT_ACTION, dataMessageRetry);
									break;
								case Message.ID_MESSAGE_SUCCESS :
									this.log("Coup valide pris en compte !");
									game.validateAction();
									break;
								case Message.ID_MESSAGE_PLAYER_ACTION :
									int betValue = dataReader.getBetValue();
									int idPlayer = dataReader.getIdPlayerAction();
									game.addBetPlayer(idPlayer, betValue);
									break;
								case Message.ID_MESSAGE_HAND_START :
									game.setStateGame(StateGame.HAND_BEGIN);
									Player[] playersHand = dataReader.getPlayers();
									game.startHand(playersHand);
									break;
								case Message.ID_MESSAGE_HAND_END :
									game.setStateGame(StateGame.HAND_FINISH);
									Player[] winners = dataReader.getWinners();
									game.finishHand(winners);
									break;
								case Message.ID_MESSAGE_PLAY_TIMEOUT :
									game.abortPlayOnTimeout();
									break;
								case Message.ID_MESSAGE_CHANGE_BLIND :
									game.updateBlindAmount(dataReader.getBlindAmount());
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
					}
				}
			}catch (SocketException e2) {
				isAlive = false;
				this.log(" [ERROR] Perte de la connexion au serveur....");
			} catch (IOException e2) {
				isAlive = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendResponse(String idMessage, JsonElement dataMessage){
		Message m = new Message();
		m.setId(idMessage);
		Map<String, JsonElement> data = new HashMap<String, JsonElement>();
		switch (idMessage){
			case Message.ID_MESSAGE_CLIENT_ACTION:
				data.put(Message.DATA_KEY_VALUE, dataMessage);
				break;
			case Message.ID_MESSAGE_JOIN_LOBBY:
				data.put(Message.DATA_KEY_NAME_TEAM, dataMessage);
				break;
		}
		m.setData(data);
		this.log("Envoi de " + m);
		writer.print(gson.toJson(m));
		writer.flush();
	}

	public void release(){
		try {
			this.log("Fermeture des flux.... avant arrêt du client");
			if (clientSocket != null){
				clientSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
