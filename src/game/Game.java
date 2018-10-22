package game;

import player.ActionPlayer;
import player.Player;
import player.StatePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import card.Card;
import card.Hand;
import constantes.Constantes;

public class Game {
	public static final int NB_MAX_HANDS = 150;
	private int nb_hand = 0;
	private int START_COINS = 1500;
	private Player ia = null;
	private Map<Integer, Player> rivals;
	private int last_bet = 0;
	private int nb_rivals = 0;
	private Timer timeout_action;
	boolean is_valid = false;
	
	public void startGame(Player p, int nb_rivals)
	{
		p.setState(StatePlayer.ACTIVE);
		p.initialize();
		ia = p;
		this.nb_rivals = nb_rivals;
		rivals = new HashMap<Integer, Player>();
	}
	
	
	private void updateInfosPlayer(Player player)
	{
		if (ia.isSameThan(player))
		{
			ia.updateInfoFrom(player);
		}
		else
		{
			if (rivals.containsKey(player.getId()))
			{
				Player rival = rivals.get(player.getId());
				rival.updateInfoFrom(player);
			}
			else
			{
				player.initialize();
				rivals.put(player.getId(), player);
			}
		}
	}
	
	public void startHand(Player[] players_hand)
	{
		nb_hand++;
		if (nb_hand <= NB_MAX_HANDS)
		{
			last_bet = 0;
			ia.startNewHand();
			for (Player player : players_hand) {
				this.updateInfosPlayer(player);
			}
			
		}
	}
	
	public void addNewCards(ArrayList<Card> cards)
	{
		ia.addCards(cards);
	}
	
	public void finishHand(Player[] winners)
	{
		for (Player player : winners) {
			if (ia.isSameThan(player))
			{
				ia.uptdateVariationCoinsEndHand(player.getNbCoins());
				break;
			}
			this.updateInfosPlayer(player);
		}
	}
		
	public void validateAction()
	{
		ia.validateAction();
		is_valid = true;
		timeout_action.cancel();
		timeout_action = null;
		System.out.println(System.currentTimeMillis());
	}
	
	public ActionPlayer doAction()
	{
		ActionPlayer action = null;
		if (! ia.isFolded())
		{
			timeout_action = new Timer();
			System.out.println(System.currentTimeMillis());
			timeout_action.schedule(new TimerTask() {
				
				@Override
				public void run() {
					if (!is_valid)
					{
						System.out.println("Timeout !");
						if (last_bet != 0)
						{
							ia.setState(StatePlayer.FOLDED);
						}
					}
					else
					{
						System.out.println("Coup validé dans les temps....");
					}
				}
			}, Constantes.TIME_BEFORE_TIMEOUT * 1000);
			
			action = ia.doAction(nb_hand, last_bet);
		}
		
		return action;
	}
	
	public Hand getHand() {
		
		return ia.getHand();
	}

	public Player getPlayer() {
		return ia;
	}

	public void addActionOtherPlayer(int id_player, ActionPlayer action_other) {
		if (id_player != ia.getId())
		{
			Player rival = rivals.get(id_player);
			if (rival != null)
				rival.updateCoinsAfterAction(action_other);
			else
				System.out.println("Attention, pas de joueur n°" + id_player + " enregistré...");
		}
		else
		{
			ia.updateCoinsAfterAction(action_other);
		}
		last_bet = action_other.getValue();
	}

	public void checkWinner(Player winner) {
		if (ia.isSameThan(winner))
		{
			System.out.println("Le vainqueur.... c'est...... MOI !!");
		}
		else
		{
			System.out.println("Ah... comment.... j'ai pas gagné ? Zut !");
		}
	}


	public void abortPlayOnTimeout() {
		System.out.println("Action annulée... Timeout !");
	}
}
