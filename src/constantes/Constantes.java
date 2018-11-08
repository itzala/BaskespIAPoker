package constantes;

public interface Constantes {
	public final int SERVER_PORT			= 4000;
	public final String SERVER_ADDRESS		= "127.0.0.1";
	
	public final String DATA_DIRECOTY		= System.getProperty("user.dir") + "/data";
	public final String DATAFILE 			= Constantes.DATA_DIRECOTY + "/next_input.json";
	public final String OUTPUT_SERVER		= System.getProperty("user.dir") + "/log/server.out";
	public final String OUTPUT_CLIENT		= System.getProperty("user.dir") + "/log/client.out";
	
	public final String PLAYER_NAME			= "Baskesp";
	
	public final int TIME_BEFORE_TIMEOUT 	= 15;	// temps en secondes
	
	public final int NB_MAX_CARDS			= 7;
	
	public final String INFO_NB_HAND 		= "info.nb_hand";
	public final String INFO_NB_RIVALS 		= "info.nb_rivals";
	public final String INFO_STATE_GAME		= "info.state_game";
	public final String INFO_LAST_BET		= "info.last_bet";
	public final String INFO_SMALL_BLIND	= "info.small_blind";
	public final String INFO_BIG_BLIND		= "info.big_blind";
}
