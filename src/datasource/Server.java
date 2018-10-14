package datasource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import card.Card;
import constantes.Constantes;
import player.Player;

public class Server {
	private ServerSocket server;
	private Gson parser;
	private Socket s_client;
	private JsonReader reader;
	private PrintWriter writer;
	private BufferedReader socket_reader;
	private boolean shutdown = false;
	
	public Server(int port)
	{
		parser = new Gson();
		try {
			reader = new JsonReader(new FileReader(Constantes.DATAFILE));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}	
		try {
			server = new ServerSocket(port);
			System.out.println("Lancement du serveur sur le port " + Constantes.SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isShutdown()
	{
		return shutdown;
	}
	
	public void parseInputFile()
	{
		try{
			Message[] messages = parser.fromJson(reader, Message[].class);
			if (messages != null && messages.length > 0)
			{
				for (Message m : messages) {
					System.out.println("Envoi du message : " + m );
					this.send(m);
					switch (m.getId()) {
					case Message.ID_MESSAGE_GAME_END:
						shutdown = true;
						System.out.println("Fin de jeu.... déconnexion du serveur.....");
						break;
					}
				}
			}
			else
			{
				System.out.println("Pas de données à traiter....");
			}
		}
		catch(JsonSyntaxException mfs)
		{
			System.out.println("Fichier d'input mal formaté !!! Veuillez corriger votre saisie et sauvegarder pour que cela soit pris en compte....");
		}
	}
	
	public void parseNewDataInputFile()
	{
		if (reader != null)
		{
			try {
				reader.close();
				reader = new JsonReader(new FileReader(Constantes.DATAFILE));
				parseInputFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void listen()
	{
		try 
		{
			s_client = server.accept();
			System.out.println("Nouvelle connexion !");
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s_client.getOutputStream())), true);
			socket_reader = new BufferedReader(new InputStreamReader(s_client.getInputStream()));
			
			
			String received_data = "";
			
			while (! s_client.isClosed() && (received_data = socket_reader.readLine()) != null) 
			{
				Message m = parser.fromJson(received_data, Message.class);
				DataReader data_reader = new DataReader();
				data_reader.setMessage(m);
				System.out.println("----------------------------------------------");
				switch (m.getId()) {
					case Message.ID_MESSAGE_JOIN_LOBBY:
						System.out.println("L'équipe " + m.getData().get(Message.DATA_KEY_NAME_TEAM) + " a rejoint le lobby" );
					break;
					case Message.ID_MESSAGE_CLIENT_ACTION:
						System.out.println("Le player a joue " + m.getData().get(Message.DATA_KEY_ACTION));
					break;
					default:
						System.out.println("Message '" + m.getId() + "' non géré...");
						System.out.println(m.getData());
					break;
				}
			}
			
		}
		catch (SocketException e) {			
			System.out.println("Client déconnecté !");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public void send(Message m)
	{
		if (writer !=  null)
		{
			String message_socket = parser.toJson(m);
			writer.println(message_socket);
			writer.flush();
		}
		else
		{
			System.out.println("Pas de connexion !");
		}
	}
	
	public void disconnect()
	{
		try {
			System.out.println("Fermeture des flux.....");
			writer.close();
			//socket_reader.close();
			s_client.close();
			server.close();
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
