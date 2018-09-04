package player;

public class ActionPlayer {
	private int value;
	
	public ActionPlayer()
	{
		this(0);
	}
	
	public ActionPlayer(int v)
	{
		if (v < 0)
			value = 0;
		else
			value = v;
	}
	
	public int getValue()
	{
		return value;
	}
}
