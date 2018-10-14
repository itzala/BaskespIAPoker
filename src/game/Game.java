package game;

import player.ActionPlayer;
import player.Player;
import player.StatePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import card.Card;
import card.Hand;

public class Game {
	public static final int NB_MAX_HANDS = 150;
	private int nb_hand = 0;
	private int START_COINS = 1500;
	private Player ia = null;
	private Map<Integer, Player> rivals;
	private int last_bet = 0;
	private int nb_rivals = 0;
	
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
				ia.winHand();
				break;
			}
			this.updateInfosPlayer(player);
		}
	}
		
	public void validateAction()
	{
		ia.validateAction();
	}
	
	public ActionPlayer doAction()
	{
		ActionPlayer action = null;
		if (! ia.isFolded())
		{
			boolean is_valid = false;
			
			while (!is_valid)
			{
				action = ia.doAction(nb_hand, last_bet);
				int coins_bet = action.getValue();
				is_valid =  (coins_bet >= 0 && coins_bet <= ia.getNbCoins());
			}
		}
		
		return action;
	}
	
	public void betBlind(ActionPlayer action)
	{
		ia.betBlind(action.getValue());
	}

	public Hand getHand() {
		
		return ia.getHand();
	}

	public Player getPlayer() {
		return ia;
	}

	public void addActionOtherPlayer(int id_player, ActionPlayer action_other) {
		Player rival = rivals.get(id_player);
		if (rival != null)
			rival.addRivalAction(action_other);
		else
			System.out.println("Attention, pas de joueur n°" + id_player + " enregistré...");
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
