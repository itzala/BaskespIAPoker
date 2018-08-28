package card;

public class Card implements Comparable<Card>{
	
	private ValueCard value;
	private ColorCard color;
	
	public Card(String value, String color)
	{
		setColorCard(color);
		setValueCard(value);
	}
	
	
	private void setColorCard(String value)
	{
		switch (value.toUpperCase()) {
		case "SPADE":
			this.color = ColorCard.SPADE;
			break;
		case "HEART":
			this.color = ColorCard.HEART;
			break;
		case "DIAMOND":
			this.color = ColorCard.DIAMOND;
			break;
		case "CLUB":
			this.color = ColorCard.CLUB;
			break;
		default:
			this.color = ColorCard.NONE;
			break;
		}
	}
	
	
	private void setValueCard(String value)
	{
		switch (value.toUpperCase()) {
		case "1":
			this.value = ValueCard.AS;
			break;
		case "2":
			this.value = ValueCard.TWO;
			break;
		case "3":
			this.value = ValueCard.THREE;
			break;
		case "4":
			this.value = ValueCard.FOUR;
			break;
		case "5":
			this.value = ValueCard.FIVE;
			break;
		case "6":
			this.value = ValueCard.SIX;
			break;
		case "7":
			this.value = ValueCard.SEVEN;
			break;
		case "8":
			this.value = ValueCard.EIGHT;
			break;
		case "9":
			this.value = ValueCard.NINE;
			break;
		case "10":
			this.value = ValueCard.TEN;
			break;
		case "JACK":
			this.value = ValueCard.JACK;
			break;
		case "QUEEN":
			this.value = ValueCard.QUEEN;
			break;
		case "KING":
			this.value = ValueCard.KING;
			break;
		default:
			this.value = ValueCard.NONE;
			break;
		}
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
	
	public boolean isColorStrongerThan(Card c)
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
