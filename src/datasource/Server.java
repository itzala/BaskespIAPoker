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
	
	public void parseInputFile()
	{
		try{
		Message[] messages = parser.fromJson(reader, Message[].class);
		for (Message m : messages) {
			System.out.println("Envoi du message : " + m );
			this.send(m);
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
			
			parseInputFile();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public void send(Message m)
	{
		String message_socket = parser.toJson(m);
		writer.println(message_socket);
		writer.flush();
	}
	
	public void disconnect()
	{
		writer.close();
		
		try {
			socket_reader.close();
			s_client.close();
			server.close();
			reader.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
