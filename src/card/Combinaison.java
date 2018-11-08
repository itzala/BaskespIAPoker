package card;
import java.util.ArrayList;

public class Combinaison implements Comparable<Combinaison>{
	private CombinaisonKind kind;
	private ArrayList<Card> cards;
	private Combinaison subCombinaison1;
	private Combinaison subCombinaison2;
	private Card bestCard;
		
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
		else {
			cards = c;
		}
		subCombinaison1 = null;
		subCombinaison2 = null;
		bestCard = getBestCard();
	}
	
	public Combinaison(CombinaisonKind kind, Combinaison c1, Combinaison c2)
	{
		if (c1 != null && c2 != null)
		{
			this.kind = kind;
			subCombinaison1 = c1;
			subCombinaison2 = c2;
			cards = null;
		}
	}
	
	public int getPowerfull()
	{
		if (subCombinaison1 != null && subCombinaison2 != null)
			return (subCombinaison1.getPowerfull() + subCombinaison2.getPowerfull());
		else
			return this.kind.ordinal() * bestCard.getIntValue();
	}
	
	public CombinaisonKind getKind()
	{
		return this.kind;
	}
	
	public Card getBestCard()
	{
		if (cards != null) // combinaison "atomique" telle que le carre, le brelan, la paire, etc...
		{
			Card bestCard = null;
			for (Card card : cards) {
				if (card.isStrongerThan(bestCard))
					bestCard = card;
			}
			return bestCard;
		}
		else // combinaison composée : full ou double paire
		{
			if (subCombinaison1.isStrongerThan(subCombinaison2))
				return subCombinaison1.getBestCard();
			else
				return subCombinaison2.getBestCard();
		}
	}
	
	public Card getWeakestCard()
	{
		if (cards != null) // combinaison "atomique" telle que le carre, le brelan, la paire, etc...
		{
			Card weakestCard = null;
			for (Card card : cards) {
				if (card.isWeakerThan(bestCard))
					weakestCard = card;
			}
			return weakestCard;
		}
		else // combinaison composée : full ou double paire
		{
			if (subCombinaison1.isStrongerThan(subCombinaison2))
				return subCombinaison1.getWeakestCard();
			else
				return subCombinaison2.getWeakestCard();
		}
	}
	
	public boolean isStrongerThan(Combinaison c)
	{
		if (c == null){
			return true;
		}
		return compareTo(c) == 1;
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
			ArrayList<Card> subCards = subCombinaison1.getCards(); 
			subCards.addAll(subCombinaison2.getCards());
			return subCards;
		}
			
	}

	@Override
	public int compareTo(Combinaison c) {
		boolean strongerKind = kind.ordinal() > c.kind.ordinal();
		if (!strongerKind) {
			Card bestCard = getBestCard();
			if (bestCard == null){
				return -1;
			}
			Card otherBestCard = c.getBestCard();
			if (otherBestCard == null){
				return 1;
			}
			return bestCard.compareTo(otherBestCard);
		}
		return 1;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(kind);
		if (subCombinaison1 == null && subCombinaison2 == null) {
			builder.append(", Cartes = ");
			for (Card card : cards) {
				builder.append(card);
			}
			
		}
		else
		{
			builder.append(", Composee de ");
			builder.append(subCombinaison1);
			builder.append("\n et de ");
			builder.append(subCombinaison2);
		}
		
		builder.append("]");
		return builder.toString();
	}
	
	
}
