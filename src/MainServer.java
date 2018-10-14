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
		
		System.out.println("Surveillance de " + Constantes.DATA_DIRECOTY);
		Path path = Paths.get(Constantes.DATA_DIRECOTY);
		try {
			WatchService watcher = path.getFileSystem().newWatchService();
			path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
			System.out.println("[SERVEUR] En attente d'une nouvelle connexion....");
			Thread ecoute_client = new Thread(new Runnable(){

				@Override
				public void run() {
					server.listen();
				}
				
			});
			
			Thread check_data = new Thread(new Runnable(){
				@Override
				public void run() {
					while(!server.isShutdown())
					{
						WatchKey watckKey;				
						try {
							watckKey = watcher.take();					
							List<WatchEvent<?>> events = watckKey.pollEvents();
							for (@SuppressWarnings("rawtypes") WatchEvent event : events) {
				                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
				                	String modified_file = event.context().toString();
				                    if (Constantes.DATAFILE.equals(Constantes.DATA_DIRECOTY + "/" + modified_file)){
				                    	System.out.println("[SERVEUR] Nouvelles données à envoyer....");
				                    	server.parseNewDataInputFile();
				                    }
				                }
				           }
							
							watckKey.reset();
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					server.disconnect();
				}
			});
			
			ecoute_client.start();
			check_data.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
