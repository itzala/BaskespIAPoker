package player;

import card.Card;

public class Player {
	private int id;
	private String name;
	private int number_coin;
	private StatePlayer state;
	private boolean isDealer;
	private Card[] cards = new Card[7];
	
	public Player(int id_player, String name, int coins, StatePlayer state_player)
	{
		this(id_player, name, coins, state_player, false);
	}
	
	public Player(int id_player, String name, int coins, StatePlayer state_player, boolean isDealer)
	{
		this.id = id_player;
		this.name = name;
		this.state = state_player;
		this.isDealer = isDealer;
	}
}
