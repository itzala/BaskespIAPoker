package card;
import java.util.ArrayList;
import java.util.Arrays;

public class Combinaison implements Comparable<Combinaison>{
	private ArrayList<Card> cards;
	private CombinaisonKind kind;
	
	public Combinaison(CombinaisonKind kind)
	{
		this(kind, null);
	}
	
	public Combinaison(CombinaisonKind kind, ArrayList<Card> c)
	{
		this.kind = kind;
		cards.addAll(c);
	}
	
	public Card getBestCard()
	{
		Card best_card = new Card(ValueCard.NONE, ColorCard.NONE);
		
		for (Card card : cards) {
			if (card.compareTo(best_card) >= 0)
				best_card = card;
		}
		return best_card;
	}
	
	public boolean isStrongerThan(Combinaison c)
	{
		return c != null && compareTo(c) == 1;
	}
	
	public boolean isValid()
	{
		return this.kind != CombinaisonKind.NONE;
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
}
