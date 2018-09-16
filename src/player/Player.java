package player;

import java.util.ArrayList;

import card.Card;
import card.CombinaisonKind;
import card.Hand;
import game.Game;

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
	
	private int getPowerOfHand(int nb_hand, int last_bet)
	{
		int ratio_hands = (int) (nb_hand * 100 / Game.NB_MAX_HANDS);
		int ratio_bet = (int) (last_bet * 100/ nb_coins);
		int rank_combinaison = this.current_hand.getBestCombinaison().getPowerfull();
	
		if (rank_combinaison >= CombinaisonKind.values().length / 2)
			return 50;
		else
			return (int) (Math.random() * 100);
	}
		
	public ActionPlayer doAction(int nb_hand, int last_bet)
	{
		int power_hand = this.getPowerOfHand(nb_hand, last_bet);
		
		if (power_hand >= 80) // notre main est suffisament forte, on relance 
		{
			current_action = new ActionPlayer((int)(Math.round(last_bet * 1.5)));;
		}
		else if (power_hand >= 50 && power_hand < 80) // on call
		{
			current_action = new ActionPlayer(last_bet);
		}
		else if (power_hand >= 20 && power_hand < 50) // on check
		{
			current_action = new ActionPlayer();
		}
		else // on se couche
		{
			current_action = new ActionPlayer();
		}
				
		return current_action;
	}
	
	public void betBlind(int blind)
	{
		current_action = new ActionPlayer(blind);
		validateAction();
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
		System.out.println("Fin d'ajout de la carte");
	}
	
	public Player addCards(ArrayList<Card> list_card)
	{
		for (Card c : list_card) {
			
			addNewCard(c);
		}
		System.out.println("Fin d'ajout de toutes les cartes");
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
	
	public boolean isSameThan(Player other)
	{
		return this.id == other.id && this.name.equals(other.name);
	}
	
	public void winHand()
	{
		current_hand.winHand();
	}

	public void addCoins(int nbCoins) {
		this.nb_coins += nbCoins;
		
	}

	public Hand getHand() {
		
		return current_hand;
	}
}
