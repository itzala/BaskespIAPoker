package player;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

import card.Card;
import card.CombinaisonKind;
import card.Hand;
import game.Game;

public class Player implements Comparable<Player>{
	private int id;
	private String name;
	private @SerializedName("chips") int nb_coins;
	private ActionPlayer current_action;
	private StatePlayer state;
	private boolean dealer;
	private ArrayList<Hand> hands;
	private Hand current_hand;
	private ArrayList<ActionPlayer> actions;
	private int nb_coins_begin_hand = 0;
	private int small_blind = 0;
	private int big_blind = 0;
	
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
		
		this.initialize();
	}
	
	public void initialize()
	{
		System.out.println("Initialisation du joueur...");
		current_action = null;
		hands = new ArrayList<Hand>();
		current_hand = new Hand();
		actions = new ArrayList<ActionPlayer>();
	}
	
	public int getId()
	{
		return this.id;
	}
	
	private int getPowerOfHand(int nb_hand, int last_bet)
	{
		System.out.println("[DEBUG] nb_hand =" + nb_hand + ", last_bet = " + last_bet );
		int ratio_hands = Math.round((nb_hand / Game.NB_MAX_HANDS) * 100);
		int ratio_bet = Math.round((last_bet / this.nb_coins) * 100);
		int rank_combinaison = this.current_hand.getBestCombinaison().getPowerfull();
		System.out.println("[DEBUG] ratio_hands =" + ratio_hands + ", ratio_bet = " + ratio_bet + ", rank_combinaison = " + rank_combinaison );		
		return (int) Math.max(Math.round((ratio_hands + ratio_bet + rank_combinaison) * 0.75), nb_coins);
	}
	
	public void updateBlindAmount(int small, int big)
	{
		small_blind = small;
		big_blind = big;
	}
		
	public ActionPlayer doAction(int nb_hand, int last_bet)
	{
		int power = getPowerOfHand(nb_hand, last_bet);
		System.out.println("Power of Hand : " + power + " et last_bet : " + last_bet);
		int next_bet = Math.max(last_bet * power, nb_coins);
		if (next_bet == nb_coins)
			this.setState(StatePlayer.ALL_IN);
		current_action = new ActionPlayer(next_bet, last_bet, small_blind, big_blind);
		System.out.println("L'ia choisie de faire : " + current_action);
			
		return current_action;
	}
	
	public void validateAction()
	{
		if (current_action != null)
		{
			this.nb_coins -= this.current_action.getValue();
			actions.add(current_action);
			current_action = null;
		}
		else
		{
			System.out.println("Pas d'action courrante d'enregistrée.... ");
		}
	}
	
	public void updateCoinsAfterAction(ActionPlayer action)
	{
		this.current_action = action;
		this.validateAction();
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
	
	public void startNewHand(int index_hand)
	{
		if (index_hand > 1)
			hands.add(current_hand);
		current_hand = new Hand();
		this.nb_coins_begin_hand = this.nb_coins;
	}
	
	public boolean isSameThan(Player other)
	{
		return this.compareTo(other) == 0;
	}
	
	public void uptdateVariationCoinsEndHand(int nb_coins_end_hand)
	{
		current_hand.setCoinsVariation(nb_coins_end_hand - nb_coins_begin_hand);
	}
	
	public Hand getHand() {
		
		return current_hand;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Player [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", nb_coins=");
		builder.append(nb_coins);
		builder.append(", current_action=");
		builder.append(current_action);
		builder.append(", state=");
		builder.append(state);
		builder.append(", dealer=");
		builder.append(dealer);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(Player other) {
		if (this.id == other.id && this.name.equals(other.name))
			return 0;
		return 1;
	}

	public void setState(StatePlayer active) {
		this.state = active;
		
	}
	
	public void updateInfoFrom(Player other) {
		this.state = other.state;
		this.dealer = other.dealer;
		this.nb_coins = other.nb_coins;
		
	}
}
