package game;

import player.ActionPlayer;
import player.Player;
import player.StatePlayer;

import java.util.ArrayList;

import card.Card;

public class Game {
	private static final int NB_MAX_HANDS = 150;
	private int nb_hand = 1;
	private int START_COINS = 1500;
	private Player ia = null;
	private int small_blind = 10;
	private int big_blind = 20;
	private int last_bet = 0;
	
	public void startGame(int id_player, String name)
	{
		ia = new Player(id_player, name, START_COINS, StatePlayer.ACTIVE);		
	}
	
	public void startHand()
	{
		switch (nb_hand) {
		case 16:
			small_blind = 20;
			big_blind = 40;
			break;
		case 31:
			small_blind = 30;
			big_blind = 60;
			break;
		case 51:
			small_blind = 40;
			big_blind = 80;
			break;
		case 81:
			small_blind = 50;
			big_blind = 100;
			break;
		case 121:
			small_blind = 75;
			big_blind = 150;
			break;
		case 136:
			small_blind = 100;
			big_blind = 200;
			break;
		default:
			break;
		}
		nb_hand++;
		if (nb_hand <= NB_MAX_HANDS)
			ia.startNewHand();
	}
	
	public void addNewCards(ArrayList<Card> cards)
	{
		ia.addCards(cards);
	}
	
	public void finishHand()
	{
		
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
		boolean is_valid = false;
		
		while (!is_valid)
		{
			action = ia.doAction(nb_hand, last_bet);
			int coins_bet = action.getValue();
			is_valid =  (coins_bet >= 0 && coins_bet <= ia.getNbCoins());
		}
		
		return action;
	}
}
