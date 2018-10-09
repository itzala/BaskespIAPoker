import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Scanner;

import constantes.Constantes;
import datasource.Server;

public class MainServer {

	public static void main(String[] args) {
		Server server = new Server(Constantes.SERVER_PORT);
	
		boolean end = false;
		
		System.out.println("Surveillance de " + Constantes.DATA_DIRECOTY);
		Path path = Paths.get(Constantes.DATA_DIRECOTY);
		try {
			WatchService watcher = path.getFileSystem().newWatchService();
			path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
			System.out.println("En attente d'une nouvelle connexion....");
			server.listen();
			while(!end)
			{
				WatchKey watckKey;				
				try {
					watckKey = watcher.take();					
					List<WatchEvent<?>> events = watckKey.pollEvents();
					for (WatchEvent event : events) {
		        	   System.out.println("Evenement " + event.kind() + " capte....");
		                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
		                	String modified_file = event.context().toString();
		                	System.out.println("Fichier concerné : " + modified_file);
		                    if (Constantes.CONTROLFILE_CLIENT.equals(Constantes.DATA_DIRECOTY + "/" + modified_file)) {
		                    	System.out.println("Fin du servevur....");
		                    	end = true;
		                    }
		                    else if (Constantes.DATAFILE.equals(Constantes.DATA_DIRECOTY + "/" + modified_file)){
		                    	System.out.println("Nouvelles données à envoyer....");
		                    	server.parseNewDataInputFile();
		                    }
		                }
		           }
					end = ! watckKey.reset();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			server.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
