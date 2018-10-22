package constantes;

public interface Constantes {
	public final int SERVER_PORT			= 1300;
	public final String SERVER_ADDRESS		= "127.0.0.1";
	
	public final String DATA_DIRECOTY		= System.getProperty("user.dir") + "/data";
	public final String DATAFILE 			= Constantes.DATA_DIRECOTY + "/next_input.json";
	public final String OUTPUT_SERVER		= System.getProperty("user.dir") + "/log/server.out";
	public final String OUTPUT_CLIENT		= System.getProperty("user.dir") + "/log/client.out";
	
	public final String PLAYER_NAME			= "Baskesp";
	
	public final int TIME_BEFORE_TIMEOUT 	= 15;	// temps en secondes
}
