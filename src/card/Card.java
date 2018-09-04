package card;

public class Card implements Comparable<Card>{
	
	private ValueCard value;
	private ColorCard color;
	private int index_card;
	
	
	public Card(ValueCard value, ColorCard color)
	{
		this(value, color, -1);
	}
	
	public Card(ValueCard value, ColorCard color, int index_card)
	{
		this.color = color;
		this.value = value;
		this.index_card = index_card;
	}
	
	public ValueCard getValue()
	{
		return this.value;
	}
	
	public ColorCard getColor()
	{
		return this.color;
	}
	
	public int getIndexCard()
	{
		return this.index_card;
	}
	
	public boolean isSameColor(Card c)
	{
		return this.getColor().ordinal() == c.getColor().ordinal();
	}
	
	// utilisé pour trier les cartes par couleur
	public boolean isColorGreaterThan(Card c)
	{
		return this.getColor().ordinal() > c.getColor().ordinal();
	}
	
	public boolean isSameCard(Card c)
	{
		return (compareTo(c) == 0) && isSameColor(c);
	}

	public static int getNbColors()
	{
		return ColorCard.values().length;
	}
	
	public static int getNbDifferentValues()
	{
		return ValueCard.values().length;
	}

	public int getIntValue() {
		return this.value.ordinal();
	}
	
	public int getIntColor(){
		return this.color.ordinal();
	}

	public boolean isSameValueThan(Card c)
	{
		return c != null && compareTo(c) == 0;
	}
	
	public boolean isStrongerThan(Card c)
	{
		return c != null && compareTo(c) == 1;
	}
	
	public boolean isWeakerThan(Card c)
	{
		return c != null && compareTo(c) == -1;
	}
	
	@Override
	public int compareTo(Card c) 
	{
		if (this.getValue().ordinal() == c.getValue().ordinal())
			return 0;
		if (this.getValue().ordinal() > c.getValue().ordinal())
			return 1;
		else
			return -1;
	}
}
