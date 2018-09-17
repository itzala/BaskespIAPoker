package card;
import java.util.ArrayList;

public class Combinaison implements Comparable<Combinaison>{
	private CombinaisonKind kind;
	private ArrayList<Card> cards;
	private Combinaison subcombinaison1;
	private Combinaison subcombinaison2;
		
	public Combinaison(CombinaisonKind kind)
	{
		this(kind, null);
	}
	
	public Combinaison(CombinaisonKind kind, ArrayList<Card> c)
	{
		this.kind = kind;
		
		if (c != null)
		{
			if (cards == null)
				cards = new ArrayList<Card>();
			cards.addAll(c);
		}
		else
			cards = c;
		subcombinaison1 = null;
		subcombinaison2 = null;
	}
	
	public Combinaison(CombinaisonKind kind, Combinaison c1, Combinaison c2)
	{
		if (c1 != null && c2 != null)
		{
			this.kind = kind;
			subcombinaison1 = c1;
			subcombinaison2 = c2;
			cards = null;
		}
	}
	
	public int getPowerfull()
	{
		return this.kind.ordinal();
	}
	
	public Card getBestCard()
	{
		if (cards != null) // combinaison "atomique" telle que le carre, le brelan, la paire, etc...
		{
			Card best_card = new Card(ValueCard.NONE, ColorCard.NONE);
			for (Card card : cards) {
				if (card.isStrongerThan(best_card))
					best_card = card;
			}
			return best_card;
		}
		else // combinaison composée : full ou double paire
		{
			if (subcombinaison1.isStrongerThan(subcombinaison2))
				return subcombinaison1.getBestCard();
			else
				return subcombinaison2.getBestCard();
		}
	}
	
	public boolean isStrongerThan(Combinaison c)
	{
		return c != null && compareTo(c) == 1;
	}
	
	public boolean isValid()
	{
		return this.kind != CombinaisonKind.NONE;
	}
	
	public ArrayList<Card> getCards()
	{
		if (cards != null)
			return cards;
		else
		{
			ArrayList<Card> sub_cards = subcombinaison1.getCards(); 
			sub_cards.addAll(subcombinaison2.getCards());
			return sub_cards;
		}
			
	}

	@Override
	public int compareTo(Combinaison c) {
		boolean stronger_kind = kind.ordinal() > c.kind.ordinal();
		if (!stronger_kind)
		{
			Card best_card = getBestCard();
			Card other_best_card = c.getBestCard();
			return best_card.compareTo(other_best_card);
		}
		return 1;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(kind);
		if (subcombinaison1 == null && subcombinaison2 == null)
		{
			builder.append(", Cartes = ");
			builder.append(cards);
		}
		else
		{
			builder.append(", Composee de ");
			builder.append(subcombinaison1);
			builder.append("\n et de ");
			builder.append(subcombinaison2);
		}
		
		builder.append("]");
		return builder.toString();
	}
	
	
}
