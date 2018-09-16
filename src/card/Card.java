package card;

import java.util.Comparator;

public class Card implements Comparable<Card>{
	
	private ValueCard value;
	private ColorCard color;
	private int index_card;
	
	
	public int getIndex_card() {
		return index_card;
	}

	public void setIndex_card(int index_card) {
		this.index_card = index_card;
	}

	
	public void setValue(String data)
	{
		System.out.println("using setValue => " + data);
	}

	public void setColor(ColorCard color) {
		this.color = color;
	}

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
	
	public boolean isSameColor(Card c)
	{
		return this.getColor().ordinal() == c.getColor().ordinal();
	}
	
	// utilisé pour trier les cartes par couleur
	public boolean isColorGreaterThan(Card c)
	{
		return ColorCardComparator.compare(this, c) == 1;
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
	public String toString() {
		//return "[" + value + " of " + color + ", index_card=" + index_card + "]";
		return "[" + value + " of " + color + "]";
	}

	@Override
	public int compareTo(Card c) 
	{
		if (this.getValue() == ValueCard.NONE)
			return 1;
		if (this.getValue().ordinal() == c.getValue().ordinal())
			return 0;
		if (this.getValue().ordinal() > c.getValue().ordinal())
			return 1;
		else
			return -1;
	}
	
	public static Comparator<Card> ColorCardComparator =  new Comparator<Card>(){
		
		public int compare(Card c1, Card c2){
			if (c1.getColor() == ColorCard.NONE)
				return 1;
			if (c1.getColor().ordinal() == c2.getColor().ordinal())
				return 0;
			if (c1.getColor().ordinal() > c2.getColor().ordinal())
				return 1;
			else
				return -1;
		}
	};
}
