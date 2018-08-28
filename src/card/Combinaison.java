package card;
import java.util.ArrayList;
import java.util.Arrays;

public class Combinaison implements Comparable<Combinaison>{
	private ArrayList<Card> cards;
	private CombinaisonKind kind;
	
	public Combinaison(String kind)
	{
		this(kind, null);
	}
	
	public Combinaison(String kind, ArrayList<Card> c)
	{
		setCombinaisonKind(kind);
		cards.addAll(c);
	}
	
	private void setCombinaisonKind(String kind)
	{
		switch (kind.toUpperCase()) {
		case "HIGHT_CARD" :
			this.kind = CombinaisonKind.HIGHT_CARD;
			break;
		case "PAIRE" :
			this.kind = CombinaisonKind.PAIRE;
			break;
		case "DOUBLE_PAIRE":
			this.kind = CombinaisonKind.DOUBLE_PAIRE;
			break;
		case "BRELAN":
			this.kind = CombinaisonKind.BRELAN;
			break;
		case "QUINTE":
			this.kind = CombinaisonKind.QUINTE;
			break;
		case "COLOR":
			this.kind = CombinaisonKind.COLOR;
			break;
		case "FULL":
			this.kind = CombinaisonKind.FULL;
			break;
		case "CARRE":
			this.kind = CombinaisonKind.CARRE;
			break;
		case "QUINTE_FLUSH":
			this.kind = CombinaisonKind.QUINTE_FLUSH;
			break;
		case "QUINTE_FLUSH_ROYAL":
			this.kind = CombinaisonKind.QUINTE_FLUSH_ROYAL;
			break;
		default:
			this.kind = CombinaisonKind.NONE;
			break;
		}
	}
	
	public Card getBestCard()
	{
		Card best_card = new Card("NONE", "NONE");
		
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
