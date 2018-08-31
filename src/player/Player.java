package player;

import java.util.ArrayList;

import card.Card;
import card.Hand;

public class Player {
	private int id;
	private String name;
	private int nb_coins;
	private ActionPlayer current_action;
	private StatePlayer state;
	private boolean dealer;
	private ArrayList<Hand> hands;
	private Hand current_hand;
	private ArrayList<ActionPlayer> actions;
	
	public Player(int id_player, String name, int coins, StatePlayer state_player)
	{
		this(id_player, name, coins, state_player, false);
	}
	
	public Player(int id_player, String name, int coins, StatePlayer state_player, boolean isDealer)
	{
		this.id = id_player;
		this.name = name;
		this.state = state_player;
		this.dealer = isDealer;
		if (coins < 0)
			this.nb_coins = 0;
		else
			this.nb_coins = coins;
		current_action = null;
		hands = new ArrayList<Hand>();
		current_hand = new Hand();
		actions = new ArrayList<ActionPlayer>();
	}
		
	private ActionPlayer raise(int v)
	{
		return new ActionPlayer(ActionKind.BET, v);
	}
	
	private ActionPlayer call()
	{ 
		return new ActionPlayer(ActionKind.BET);
	}
	
	private ActionPlayer fold()
	{
		return new ActionPlayer(ActionKind.FOLD);
	}
	
	public ActionPlayer doAction(int nb_hand, int last_bet)
	{
		current_action = new ActionPlayer(ActionKind.NONE); 
		return current_action;
	}
	
	public void validateAction()
	{
		this.nb_coins -= this.current_action.getValue();
		actions.add(current_action);
		current_action = null;
	}
	
	public void addNewCard(Card c)
	{
		current_hand.addCard(c);
	}
	
	public Player addCards(ArrayList<Card> list_card)
	{
		for (Card c : list_card) {
			addNewCard(c);
		}
		return this;
	}	
	
	public boolean isFolded()
	{
		return this.state == StatePlayer.FOLDED;
	}
	
	public boolean isDealer()
	{
		return this.dealer;
	}
	
	public void setDealer(boolean isDealer)
	{
		this.dealer = isDealer;
	}
	
	public int getNbCoins()
	{
		return this.nb_coins;
	}
	
	public void startNewHand()
	{
		hands.add(current_hand);
		current_hand = new Hand();
	}
}
