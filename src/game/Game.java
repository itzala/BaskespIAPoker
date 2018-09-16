package game;

import player.ActionPlayer;
import player.Player;
import player.StatePlayer;

import java.util.ArrayList;

import card.Card;
import card.Hand;

public class Game {
	public static final int NB_MAX_HANDS = 150;
	private int nb_hand = 1;
	private int START_COINS = 1500;
	private Player ia = null;
	private int last_bet = 0;
	
	public void startGame(int id_player, String name)
	{
		ia = new Player(id_player, name, START_COINS, StatePlayer.ACTIVE);		
	}
	
	public void startHand()
	{
		nb_hand++;
		if (nb_hand <= NB_MAX_HANDS)
			ia.startNewHand();
	}
	
	public void addNewCards(ArrayList<Card> cards)
	{
		ia.addCards(cards);
	}
	
	public void finishHand(Player winner)
	{
		if (ia.isSameThan(winner))
			ia.winHand();
		ia.addCoins(winner.getNbCoins());
	}
	
	public void gameOver()
	{
		
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
}
