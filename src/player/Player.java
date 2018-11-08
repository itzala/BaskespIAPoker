package player;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.annotations.SerializedName;

import card.Card;
import card.Combinaison;
import card.CombinaisonKind;
import card.Hand;
import constantes.Constantes;
import game.Game;
import player.ActionPlayer;
import player.StatePlayer;

public class Player implements Comparable<Player>{
	private int id;
	private String name;
	private @SerializedName("chips") int nbCoins;
	private ActionPlayer currentAction;
	private StatePlayer state;
	private boolean dealer;
	private ArrayList<Hand> hands;
	private Hand currentHand;
	private ArrayList<ActionPlayer> actions;
	private int nbCoinsBeginHand = 0;
	private int smallBlind = 0;
	private int bigBlind = 0;
	
	public Player(Player model)
	{
		this(model.getId(), model.getName(), model.getNbCoins(), model.getState());
	}
	
	public Player(int idPlayer, String name, int coins, StatePlayer statePlayer)
	{
		this(idPlayer, name, coins, statePlayer, false);
	}
	
	public Player(int id_player, String name, int coins, StatePlayer state_player, boolean isDealer)
	{
		this.id = id_player;
		this.name = name;
		this.state = state_player;
		this.dealer = isDealer;
		if (coins < 0)
			this.nbCoins = 0;
		else
			this.nbCoins = coins;
		
		this.initialize();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public StatePlayer getState()
	{
		return this.state;
	}
	
	private void log(String message)
	{
		System.out.println("[ Player ] " + message);
	}
	
	private void initialize()
	{
		this.log("Initialisation du joueur '" + this.name  + "'");
		currentAction = null;
		currentHand = null;
		hands = new ArrayList<Hand>();
		actions = new ArrayList<ActionPlayer>();
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public void updateBlindAmount(int small, int big)
	{
		smallBlind = small;
		bigBlind = big;
	}
	
	private Combinaison getBestCombinaison()
	{
		return currentHand.getBestCombinaison();
	}
	
	private int getNbConnectedCards()
	{
		Card bestCard = getBestCombinaison().getBestCard();
		Card weakestCard = getBestCombinaison().getWeakestCard();
		
		/*if (bestCard.getIntValue() - weakestCard.getIntValue() < 3){
			return 2;
		}
		
		if (bestCard.getIntValue() - weakestCard.getIntValue() < 5){
			return 1;
		}*/
		return 0;
		
	}
	
	private int getNbSuitedCards()
	{
		return currentHand.getNbSuitedCards();
	}
	
	public ActionPlayer doAction(HashMap<String, Object> infos)
	{
	
		int lastBet = (int) infos.get(Constantes.INFO_LAST_BET);
		int nbHand = (int) infos.get(Constantes.INFO_NB_HAND);
		int nbRivals = (int) infos.get(Constantes.INFO_NB_RIVALS);
		
		double power = 1.5;
		int nextBet = lastBet;
		Combinaison bestCombinaison = getBestCombinaison();
		if (bestCombinaison != null){
			int strength = bestCombinaison.getPowerfull();
			int quality = getNbConnectedCards() + getNbSuitedCards();
			int mIndicator = nbCoins / (bigBlind + smallBlind);
			
			if (mIndicator >= 10 && mIndicator <= 20){
				nextBet = lastBet + bigBlind;
			} else if (mIndicator >= 5){
				nextBet = lastBet + bigBlind;
			} else if (mIndicator >= 0){
				nextBet = nbCoins;
			}
		}
		
		currentAction = new ActionPlayer(nextBet, lastBet, smallBlind, bigBlind);
		
		
		
		
		/*double power = 1.5; //getPowerOfHand(nb_hand, last_bet);
		System.out.println("Power of Hand : " + power + " et last_bet : " + last_bet);
		int next_bet = (int) Math.min(last_bet * power, nb_coins);
		if (next_bet == nb_coins)
			this.setState(StatePlayer.ALL_IN);
		current_action = new ActionPlayer(next_bet, last_bet, small_blind, big_blind);
		this.log("L'ia choisie de faire : " + current_action);*/
			
		return currentAction;
	}
	
	public void validateAction()
	{
		if (currentAction != null) {
			this.nbCoins -= this.currentAction.getValue();
			actions.add(currentAction);
			currentAction = null;
		}
		else
		{
			this.log("Pas d'action courante d'enregistrée.... ");
		}
	}
	
	public void updateCoinsAfterAction(ActionPlayer action)
	{
		this.currentAction = action;
		this.validateAction();
	}
	
	public void addNewCard(Card c)
	{
		currentHand.addCard(c);
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
		return this.nbCoins;
	}
	
	public void startNewHand(int index_hand)
	{
		if (index_hand > 1){
			hands.add(currentHand);
		}
		currentHand = new Hand(index_hand);
		this.nbCoinsBeginHand = this.nbCoins;
	}
	
	public boolean isSameThan(Player other)
	{
		return this.compareTo(other) == 0;
	}
	
	public void uptdateVariationCoinsEndHand(int nbCoinsEndHand)
	{
		currentHand.setCoinsVariation(nbCoinsEndHand - nbCoinsBeginHand);
	}
	
	public Hand getHand() {
		
		return currentHand;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Player [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", nb_coins=");
		builder.append(nbCoins);
		builder.append(", current_action=");
		builder.append(currentAction);
		builder.append(", state=");
		builder.append(state);
		builder.append(", dealer=");
		builder.append(dealer);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(Player other) {
		if (this.id == other.id && this.name.equals(other.name)){
			return 0;
		}
		return 1;
	}

	public void setState(StatePlayer active) {
		this.state = active;
		
	}
	
	public void updateInfoFrom(Player other) {
		this.state = other.state;
		this.dealer = other.dealer;
		this.nbCoins = other.nbCoins;
		
	}
}
