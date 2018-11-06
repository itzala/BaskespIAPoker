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
import card.Hand_OLD;
import constantes.Constantes;
import datasource.Message;

public class Game {
	public static final int NB_MAX_HANDS = 150;
	private int nb_hand = 0;
	private int START_COINS = 1500;
	private Player ia = null;
	private Map<Integer, Player> rivals;
	private int last_bet = 0;
	private int small_blind = 0;
	private int big_blind = 0;
	private int nb_rivals = 0;
	private Timer timeout_action;
	private boolean is_valid = false;
	private StateGame state = StateGame.GAME_INITIALIZE;
	
	private void log(String message)
	{
		System.out.println("[ Game ] " + message);
	}
	
	
	public void startGame(Player p, int nb_rivals)
	{
		ia = new Player(p);
		this.nb_rivals = nb_rivals;
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
					this.log("Le joueur '"+ localPlayer.getName() + "' continue...");
					localPlayer.startNewHand(nb_hand);
				} else {
					this.log("Le joueur '"+ localPlayer.getName() + "' s'est couché...");
				}
				break;
			case HAND_FINISH:
				localPlayer.uptdateVariationCoinsEndHand(player.getNbCoins());
				break;
		}
	}
	
	public void updateBlindAmount(HashMap<String, Integer> blinds)
	{
		this.big_blind = blinds.get(Message.DATA_KEY_BIG_BLIND);
		this.small_blind = blinds.get(Message.DATA_KEY_SMALL_BLIND);
		ia.updateBlindAmount(small_blind, big_blind);
		last_bet = big_blind;
	}
	
	public void startHand(Player[] players_hand)
	{
		nb_hand++;
		if (nb_hand <= NB_MAX_HANDS){
			last_bet = 0;
			this.log("Début de la main n°" + nb_hand);
			for (Player player : players_hand) {
				this.updateInfosPlayer(player);	
			}
		} else {
			this.log("Nombre maximal de main atteint !!");
		}
	}
	
	public void addPlayerNewCards(ArrayList<Card> cards)
	{
		ia.addCards(cards);
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
		is_valid = true;
		timeout_action.cancel();
		timeout_action = null;
	}
	
	public boolean isLocalValidAction()
	{
		return true;
	}
	
	public boolean isValidAction()
	{
		return is_valid;
	}
	
	public ActionPlayer doAction()
	{
		ActionPlayer action = null;
		if (! ia.isFolded()) {
			timeout_action = new Timer();
			is_valid = false;
			timeout_action.schedule(new TimerTask() {
				
				@Override
				public void run() {
					if (!is_valid) {
						System.out.println("[Game] Timeout !");
						if (last_bet != 0) {
							ia.setState(StatePlayer.FOLDED);
						}
					} else {
						System.out.println("[Game] Coup validé dans les temps....");
					}
				}
			}, Constantes.TIME_BEFORE_TIMEOUT * 1000);
			
			action = ia.doAction(nb_hand, last_bet);
		}
		
		return action;
	}
	
	public Hand_OLD getHand() {
		
		return ia.getHand();
	}

	public Player getPlayer() {
		return ia;
	}

	public void checkWinner(Player winner) {
		if (ia.isSameThan(winner)){
			this.log("Le vainqueur.... c'est...... MOI !!");
		} else {
			this.log("Ah... comment.... j'ai pas gagné ? Zut !");
		}
	}


	public void abortPlayOnTimeout() {
		this.log("Action annulée... Timeout !");
		is_valid = false;
		timeout_action.cancel();
		timeout_action = null;
	}


	public void addBetPlayer(int id_player, int bet_value) {
		
		if (id_player != ia.getId()) {
			Player rival = rivals.get(id_player);
			if (rival != null) {
				rival.updateCoinsAfterAction(new ActionPlayer(bet_value, last_bet, small_blind, big_blind));
			} else {
				this.log("Attention, pas de joueur n°" + id_player + " enregistré...");
			}
		} else {
			ia.updateCoinsAfterAction(new ActionPlayer(bet_value, last_bet, small_blind, big_blind));
		}
		last_bet = bet_value;
	}
	
	public void setStateGame(StateGame newState)
	{
		this.state = newState;
	}
}
