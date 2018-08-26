package card;

import java.util.ArrayList;

public class Hand {
	private Card[][] cards = new Card[Card.getNbColors()][Card.getNbDifferentValues()];
	private Combinaison best_combinaison = null;
	
	private static Hand instance = null;
	
	private Hand(){}
	
	
	public static Hand getInstance()
	{
		if (instance == null )
		{
			instance = new Hand();
			instance.initializeCards();
		}
		
		return instance;
	}
	
	private void initializeCards()
	{
		for (int c = 0; c < Card.getNbColors(); c++)
		{
			for (int v = 0; v < Card.getNbDifferentValues(); v++)
			{
				cards[c][v] = null;
			}
		}
	}
	
	public Hand addCard(Card c)
	{
		cards[c.getIntValue()][c.getIntColor()] = c;
		return this;
	}
	
	public Hand addCards(ArrayList<Card> list_card)
	{
		for (Card c : list_card) {
			addCard(c);
		}
		return this;
	}
	
	
	private ArrayList<Card> getSameCaracteristicCardCombinaisons(int number_cards, int excluded_value, boolean changeDirection)
	{
		// Pour gerer les couleurs
		int borne1 = Card.getNbColors();
		int borne2 = Card.getNbDifferentValues();
		
		// Pour gerer les paires, brelans, fulls, carres
		if (changeDirection)
		{
			borne1 = Card.getNbDifferentValues();
			borne2 = Card.getNbColors();
		}
						
		for (int index_borne1 =  borne1 - 1; index_borne1 >= 0; index_borne1--)
		{
			int nb_of_value=0;
			ArrayList<Card> combinaison_cards = new ArrayList<Card>();
			
			for (int index_borne2 = borne2 - 1 ; index_borne2 >= 0; index_borne2--)
			{
				if (cards[index_borne2][index_borne1] != null && index_borne1 != excluded_value)
				{
					nb_of_value++;
					combinaison_cards.add(cards[index_borne2][index_borne1]);
				}
					
				if (nb_of_value == number_cards)
				{
					return combinaison_cards;
				}
			}
		}
		return null;
	}
	
	private Combinaison getPaire()
	{
		ArrayList<Card> list_card = getSameCaracteristicCardCombinaisons(2, -1, true);
		if (list_card != null )
		{
			ArrayList<Card> list_card2 = getSameCaracteristicCardCombinaisons(2, list_card.get(0).getIntValue(), true);
			if (list_card2 != null)
			{
				list_card.addAll(list_card2);
				return new Combinaison("DOUBLE_PAIRE", list_card);
			}
			else
				return new Combinaison("PAIRE", list_card);
		}
		return new Combinaison("NONE");
	}
	

	private Combinaison getQuinte()
	{
		// TODO	
		return new Combinaison("NONE");
	}
	
	private Combinaison getColor()
	{
		ArrayList<Card> list_card = getSameCaracteristicCardCombinaisons(5, -1, false);
		if (list_card != null )
			return  new Combinaison("COLOR", list_card);
		return new Combinaison("NONE");
	}
	
	private Combinaison getFull()
	{
		ArrayList<Card> list_card = getSameCaracteristicCardCombinaisons(3, -1, true);
		if (list_card != null )
		{
			ArrayList<Card> list_card2 = getSameCaracteristicCardCombinaisons(2, list_card.get(0).getIntValue(), true);
			if (list_card2 != null)
			{
				list_card.addAll(list_card2);
				return new Combinaison("FULL", list_card);
			}
			else
				return new Combinaison("BRELAN", list_card);
		}
		else
			return getPaire();
	}
	
	private Combinaison getCarre()
	{
		ArrayList<Card> list_card = getSameCaracteristicCardCombinaisons(4, -1, true);
		if (list_card != null )
			return  new Combinaison("CARRE", list_card);
		return new Combinaison("NONE");
	}
	
	private Combinaison getQuinteFlush()
	{
		// TODO
		return new Combinaison("NONE");
	}
	
	private Combinaison getQuinteFlushRoyale()
	{
		// TODO
		return new Combinaison("NONE");
	}
	
	private Combinaison getHighestCard()
	{
		ArrayList<Card> list_card = getSameCaracteristicCardCombinaisons(1, -1, true);
		if (list_card != null )
			return  new Combinaison("HIGHT_CARD", list_card);
		return new Combinaison("NONE");
	}
	
	public Hand calculateCombinaisons()
	{
		// TODO
		return this;
	}
	
	public Combinaison getBestCombinaison()
	{
		return best_combinaison;
	}
}
