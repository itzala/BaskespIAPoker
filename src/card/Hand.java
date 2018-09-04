package card;

import java.util.ArrayList;

public class Hand {
	private final int MAX_NB_CARDS = 7;
	private int current_nb_cards = 0;
	private Card[] cards_by_value;
	private Card[] cards_by_color;
	private Combinaison best_combinaison = new Combinaison(CombinaisonKind.NONE);
		
	private ArrayList<Card> cards_paire = null;	
	private ArrayList<Card> cards_double_paire = null ; 
	private ArrayList<Card> cards_brelan = null;
	private ArrayList<Card> cards_quinte = null; 
	private ArrayList<Card> cards_color = null;
	private ArrayList<Card> cards_full = null;
	private ArrayList<Card> cards_carre = null; 
	private ArrayList<Card> cards_quinte_flush = null; 
	private ArrayList<Card> cards_quinte_flush_royal = null; 
	
	private boolean isWon = false;
	
	
	public Hand()
	{
		initializeCards();
	}
	
	private void initializeCards()
	{
		for (int c = 0; c < MAX_NB_CARDS; c++)
		{
			cards_by_value[c] = new Card(ValueCard.NONE, ColorCard.NONE);
			cards_by_color[c] = new Card(ValueCard.NONE, ColorCard.NONE);;
		}
	}
	
	public Hand addCard(Card c)
	{	
		if (current_nb_cards == 0)
		{
			cards_by_value[0] = c;
			cards_by_color[0] = c;
		}
		else
		{
			// On trie les cartes par valeur pour detecter facilement les paires, double paires, brelan
			// full, carree, suite
			for (int i = 0; i < current_nb_cards; i++)
			{
				Card tmp = cards_by_value[i];
				int j = i - 1;
				while (j > 0 && cards_by_value[j - 1].isStrongerThan(c))
				{
					cards_by_value[j] = cards_by_value[j - 1];
					j--;
				}
				cards_by_value[j] = tmp;
			}
			
			// On trie les cartes par couleurs pour detecter facilement les couleurs
			for (int i = 0; i < current_nb_cards; i++)
			{
				Card tmp = cards_by_color[i];
				int j = i - 1;
				while (j > 0 && cards_by_color[j - 1].isColorGreaterThan(c))
				{
					cards_by_color[j] = cards_by_color[j - 1];
					j--;
				}
				cards_by_color[j] = tmp;
			}
			
		}
		current_nb_cards++;
		calculateCombinaisons();
		
		return this;
	}
	
	public Hand addCards(ArrayList<Card> list_card)
	{
		for (Card c : list_card) {
			addCard(c);
		}
		return this;
	}
			
	private ArrayList<Card> calculatePaire(int excluded_value, boolean is_double_paire)
	{
		if (current_nb_cards >= 2)
		{
			Card card_to_compare = cards_by_value[current_nb_cards - 1];
			
			for(int i = current_nb_cards - 2 ; i > 0; i--)
			{
				Card compared_card = cards_by_value[i]; 
				if (compared_card.getIntValue() != excluded_value && compared_card.isSameValueThan(card_to_compare))
				{
					ArrayList<Card> cards_combinaison = new ArrayList<Card>();
					if (!is_double_paire)
					{
						cards_paire.add(card_to_compare);
						cards_paire.add(compared_card);
					}
					else // pour ne pas ecraser la premiere paire qui sera forcement plus grande que la deuxieme
					{
						cards_combinaison.add(card_to_compare);
						cards_combinaison.add(compared_card);
						return cards_combinaison;
					}
					break;
				}
				else
					card_to_compare = compared_card;
			}
		}
		return null;
	}
	
	private void calculateDoublePaire()
	{
		if (current_nb_cards >= 4)
		{
			calculatePaire(-1, false);
			if (cards_paire != null && ! cards_paire.isEmpty())
			{
				ArrayList<Card> cards_second_paire = calculatePaire(cards_paire.get(0).getIntValue(), true);
				if (cards_paire != null && ! cards_paire.isEmpty())
				{
					cards_double_paire.addAll(cards_paire);
					cards_double_paire.addAll(cards_second_paire);
				}
			}
		}
		
	}
	
	private void calculateBrelan()
	{
		if (current_nb_cards >= 3)
		{
			Card card_to_compare = cards_by_value[current_nb_cards - 1];
			int i = current_nb_cards - 2;
			while(i >= 1)
			{
				Card compared_card1 = cards_by_value[i];
				Card compared_card2 = cards_by_value[i-1];
				if (compared_card1.isSameValueThan(card_to_compare))
				{
					if (compared_card2.isSameValueThan(card_to_compare))
					{
						cards_brelan.add(card_to_compare);
						cards_brelan.add(compared_card1);
						cards_brelan.add(compared_card2);
						break;
					}
					else
					{
						card_to_compare = compared_card2;
						i -= 2;
					}
				}
				else
				{
					card_to_compare = compared_card1;
					i--;
				}
					
			}
		}
		
	}
		
	private void calculateCarre()
	{
		if (current_nb_cards >= 4)
		{
			Card card_to_compare = cards_by_value[current_nb_cards - 1];
			int i = current_nb_cards - 2;
			while(i >= 2)
			{
				Card compared_card1 = cards_by_value[i];
				Card compared_card2 = cards_by_value[i-1];
				Card compared_card3 = cards_by_value[i-1];
				if (compared_card1.isSameValueThan(card_to_compare))
				{
					if (compared_card2.isSameValueThan(card_to_compare))
					{
						if (compared_card3.isSameValueThan(card_to_compare))
						{
							cards_carre.add(card_to_compare);
							cards_carre.add(compared_card1);
							cards_carre.add(compared_card2);
							cards_carre.add(compared_card3);
							break;
						}
						else
						{
							card_to_compare = compared_card3;
							i -= 3;
						}
					}
					else
					{
						card_to_compare = compared_card2;
						i -= 2;
					}
				}
				else
				{
					card_to_compare = compared_card1;
					i--;
				}
			}
		}
		
	}
	
	private void calculateColor()
	{
		if (current_nb_cards >= 5)
		{
			Card card_to_compare = cards_by_color[0];
			int i = 1;
			int nb_cards_same_color = 1;
			while (i < current_nb_cards - 1 && nb_cards_same_color < 5)
			{
				Card compared_card = cards_by_color[i];
				if (card_to_compare.isSameColor(compared_card))
				{
					nb_cards_same_color++;
					if (nb_cards_same_color == 5){				
						cards_color.add(cards_by_color[i - 4]);
						cards_color.add(cards_by_color[i - 3]);
						cards_color.add(cards_by_color[i - 2]);
						cards_color.add(cards_by_color[i - 1]);
						cards_color.add(cards_by_color[i]);
					}
				}
				else
					nb_cards_same_color = 0;
				i++;
			}
		}
		
	}
	
	private void calculateFull()
	{
		if (current_nb_cards >= 5)
		{
			calculateBrelan();
			if (cards_brelan != null && ! cards_brelan.isEmpty())
			{
				calculatePaire(cards_brelan.get(0).getIntValue(), false);
				if (cards_paire != null && ! cards_paire.isEmpty())
				{
					cards_full.addAll(cards_brelan);
					cards_full.addAll(cards_paire);
				}
			}

		}
		
	}
	
	private void calculateQuinte()
	{
		if (current_nb_cards >= 5)
		{
			Card card_to_compare = cards_by_value[0];			
			int i = 1;
			int nb_cards_quinte = 0;
			int next_value_quintable = card_to_compare.getIntValue() +1;
			int index_last_card = 0;
			
			if (card_to_compare.getValue() == ValueCard.TWO 
				&& cards_by_value[current_nb_cards - 1].getValue() == ValueCard.AS)
			{
				cards_quinte.add(cards_by_value[current_nb_cards - 1]);				
				nb_cards_quinte = 2;
				index_last_card = current_nb_cards - 1;
			}
			else
			{
				nb_cards_quinte = 1;
				index_last_card = current_nb_cards;
			}
			
			cards_quinte.add(card_to_compare);
			
			while (i < index_last_card && nb_cards_quinte < 5)
			{
				Card compared_card = cards_by_value[i];
								
				if (compared_card.getIntValue() == next_value_quintable)
				{
					next_value_quintable++;
					nb_cards_quinte++;
					cards_quinte.add(compared_card);
				}
				i++;
			}
			if (nb_cards_quinte != 5)
			{
				cards_quinte.clear();
			}
		}
		
	}
	
	private void calculateQuinteFlush()
	{
		if (current_nb_cards >= 5)
		{
			calculateQuinte();
			if (cards_quinte != null && ! cards_quinte.isEmpty())
			{
				boolean same_color = true;
				Card card_to_compare = cards_quinte.get(0);
				for (int i = 1; i < cards_quinte.size(); i++)
				{
					if (! cards_quinte.get(i).isSameColor(card_to_compare))
					{
						same_color = false;
						break;
					}
				}
				if (same_color)
					cards_quinte_flush = cards_quinte;
			}
		}
		
	}
	
	private void calculateQuinteFlushRoyale()
	{
		if (current_nb_cards >= 5)
		{
			calculateQuinteFlush();
			if (cards_quinte_flush != null && ! cards_quinte_flush.isEmpty()
					&& cards_quinte_flush.get(cards_quinte_flush.size() -1).getValue() == ValueCard.AS)
				cards_quinte_flush_royal = cards_quinte_flush;
		}
	}
		
	private Card calcultateHighestCard()
	{
		return cards_by_value[current_nb_cards];
	}
	
	public Hand calculateCombinaisons()
	{
		calculateQuinteFlushRoyale();
		
		Combinaison new_combinaison = null;
		
		if (cards_quinte_flush_royal != null && ! cards_quinte_flush_royal.isEmpty())
		{
			new_combinaison = new Combinaison(CombinaisonKind.QUINTE_FLUSH_ROYAL, cards_quinte_flush_royal);
		}
		else if (cards_quinte_flush != null && ! cards_quinte_flush.isEmpty())
		{
			new_combinaison = new Combinaison(CombinaisonKind.QUINTE_FLUSH, cards_quinte_flush);
		}
		else
		{
			calculateCarre();
			if (cards_carre != null && ! cards_carre.isEmpty())
			{
				new_combinaison = new Combinaison(CombinaisonKind.CARRE, cards_carre);
			}
			else
			{
				calculateFull();
				if (cards_full != null && ! cards_full.isEmpty())
				{
					new_combinaison = new Combinaison(CombinaisonKind.FULL, cards_full);
				}
				else 
				{
					calculateColor();
					if (cards_color != null && ! cards_color.isEmpty())
					{
						new_combinaison = new Combinaison(CombinaisonKind.COLOR, cards_color);
					}
					else if (cards_quinte != null && ! cards_quinte.isEmpty())
					{
						new_combinaison = new Combinaison(CombinaisonKind.QUINTE, cards_quinte);
					}		
					else if (cards_brelan != null && ! cards_brelan.isEmpty())
					{
						new_combinaison = new Combinaison(CombinaisonKind.BRELAN, cards_brelan);
					}
					else
					{
						calculateDoublePaire();
						if (cards_double_paire != null && ! cards_double_paire.isEmpty())
						{
							new_combinaison = new Combinaison(CombinaisonKind.DOUBLE_PAIRE, cards_double_paire);
						}
						else if (cards_paire != null && ! cards_paire.isEmpty())
						{
							new_combinaison = new Combinaison(CombinaisonKind.PAIRE, cards_paire);
						}
						else
						{
							ArrayList<Card> hight_card = new ArrayList<Card>();
							hight_card.add(calcultateHighestCard());
							new_combinaison = new Combinaison(CombinaisonKind.HIGHT_CARD, hight_card);
						}
					}
				}
			}
		}
		
		
		if (new_combinaison.isStrongerThan(best_combinaison))
			best_combinaison = new_combinaison;
		
		
		return this;
	}
	
	public Combinaison getBestCombinaison()
	{
		return best_combinaison;
	}
	
	public void winHand()
	{
		this.isWon = true;
	}
	
	public boolean isWonHand()
	{
		return this.isWon;
	}
}
