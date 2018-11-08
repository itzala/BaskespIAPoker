package game;

import player.ActionPlayer;
import player.Player;
import player.StatePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import card.Card;
import card.Hand;
import constantes.Constantes;
import datasource.Message;

public class Game {
	public static final int NB_MAX_HANDS = 150;
	private int nbHand = 0;
	private int START_COINS = 1500;
	private Player ia = null;
	private Map<Integer, Player> rivals;
	private int lastBet = 0;
	private int smallBlind = 0;
	private int bigBlind = 0;
	private int nbRivals = 0;
	private Timer timeoutAction;
	private boolean isValid = false;
	private StateGame state = StateGame.GAME_INITIALIZE;
	
	private void log(String message)
	{
		System.out.println("[ GAME ] " + message);
	}
	
	
	public void startGame(Player p, int nb_rivals)
	{
		ia = new Player(p);
		this.nbRivals = nb_rivals;
		rivals = new HashMap<Integer, Player>();
	}
	
	private Player getLocalPlayerFor(Player player)
	{
		this.log("Récupération du localPlayer pour '"+player.getName()+"'");
		if (ia.isSameThan(player)){
			return ia;
		} else{
			return rivals.get(player.getId());
		}
	}
	
	
	private void updateInfosPlayer(Player player)
	{
		Player localPlayer = getLocalPlayerFor(player) ;
		
		if (localPlayer != null){
			localPlayer.updateInfoFrom(player);
		} else {
			localPlayer = new Player(player);
			rivals.put(player.getId(), localPlayer);
		}
		
		switch (state) {
			case HAND_BEGIN:
				if (!localPlayer.isFolded()){
					nbRivals++;
					this.log("Le joueur '"+ localPlayer.getName() + "' continue...");
					localPlayer.startNewHand(nbHand);
				} else {
					this.log("Le joueur '"+ localPlayer.getName() + "' s'est couché...");
				}
				break;
			case HAND_FINISH:
				localPlayer.uptdateVariationCoinsEndHand(player.getNbCoins());
				break;
			default :
				// on ne gère pas les autres cas ici...
				break;
		}
	}
	
	public void updateBlindAmount(HashMap<String, Integer> blinds)
	{
		this.bigBlind = blinds.get(Message.DATA_KEY_BIG_BLIND);
		this.smallBlind = blinds.get(Message.DATA_KEY_SMALL_BLIND);
		ia.updateBlindAmount(smallBlind, bigBlind);
		lastBet = bigBlind;
	}
	
	public void startHand(Player[] playersHand)
	{
		nbHand++;
		nbRivals = 0;
		if (nbHand <= NB_MAX_HANDS){
			lastBet = 0;
			this.log("=================== Début de la main n°" + nbHand + "===================");
			for (Player player : playersHand) {
				this.updateInfosPlayer(player);	
			}
		} else {
			this.log("Nombre maximal de main atteint !!");
		}
	}
	
	public void addPlayerNewCards(ArrayList<Card> cards)
	{
		ia.addCards(cards);
		int nbCards = ia.getHand().getNbCards();
		switch (nbCards){			
			case 1:
				state = StateGame.PREFLOP;
			break;
			case 3:
				state = StateGame.FLOP;
			break;
			case 6:
				state = StateGame.TURN;
			break;
			case 7:
				state = StateGame.RIVER;
			break;
		}
	}
	
	public void addBoardNewCards(ArrayList<Card> cards)
	{
		addPlayerNewCards(cards);
		for (Entry<Integer, Player> entry : rivals.entrySet()) {
			Player player = entry.getValue();
			player.addCards(cards);
		}
	}
	
	public void finishHand(Player[] winners)
	{
		this.log("Fin de la main... Mise à jour des joueurs vainqueurs ");
		for (Player player : winners) {
			this.updateInfosPlayer(player);
		}
	}
		
	public void validateAction()
	{
		ia.validateAction();
		isValid = true;
		if (timeoutAction != null){
			timeoutAction.cancel();
		}
		timeoutAction = null;
	}
	
	public boolean isValidAction()
	{
		return isValid;
	}
	
	public ActionPlayer doAction(int minBet)
	{
		ActionPlayer action = null;
		if (! ia.isFolded()) {
			timeoutAction = new Timer();
			isValid = false;
			timeoutAction.schedule(new TimerTask() {
				
				@Override
				public void run() {
					if (!isValid) {
						System.out.println("[Game] Timeout !");
						if (lastBet != 0) {
							ia.setState(StatePlayer.FOLDED);
						}
					}
				}
			}, Constantes.TIME_BEFORE_TIMEOUT * 1000);
			if (minBet != -1){
				lastBet = minBet;
			}
			
			HashMap<String, Object> infosGame = new HashMap<String, Object>();
			infosGame.put(Constantes.INFO_NB_HAND, nbHand);
			infosGame.put(Constantes.INFO_NB_RIVALS, nbRivals);
			infosGame.put(Constantes.INFO_LAST_BET, lastBet);
			infosGame.put(Constantes.INFO_STATE_GAME, state);
			infosGame.put(Constantes.INFO_SMALL_BLIND, smallBlind);
			infosGame.put(Constantes.INFO_BIG_BLIND, bigBlind);
			
			
			action = ia.doAction(infosGame);
		}
		
		return action;
	}
	
	public Hand getHand() {
		
		return ia.getHand();
	}

	public Player getPlayer() {
		return ia;
	}

	public void checkWinner(Player winner) {
		if (ia.isSameThan(winner)){
			this.log("I win !");
		} else {
			this.log("OK, I loose one fight but not the war !");
		}
	}


	public void abortPlayOnTimeout() {
		this.log("Action annulée... Timeout !");
		isValid = false;
		timeoutAction.cancel();
		timeoutAction = null;
	}


	public void addBetPlayer(int id_player, int bet_value) {
		if (id_player != ia.getId()) {
			Player rival = rivals.get(id_player);
			if (rival != null) {
				this.log("[INFO] " + rival.getName() + " vient de miser ......" + bet_value + " jetons...");
				rival.updateCoinsAfterAction(new ActionPlayer(bet_value, lastBet, smallBlind, bigBlind));
			} else {
				this.log("Attention, pas de joueur n°" + id_player + " enregistré...");
			}
		} else {
			ia.updateCoinsAfterAction(new ActionPlayer(bet_value, lastBet, smallBlind, bigBlind));
		}
		this.log("[DEBUG] Mise à jour de last_bet : " + lastBet + " en " + bet_value);
		lastBet = bet_value;
	}
	
	public void setStateGame(StateGame newState)
	{
		this.state = newState;
	}
}
