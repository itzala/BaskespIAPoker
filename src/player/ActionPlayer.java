package player;

public class ActionPlayer {
	private int value;
	private ActionKind kind;
	
	public ActionPlayer(ActionKind k)
	{
		this(k, 0);
	}
	
	public ActionPlayer(ActionKind k, int v)
	{
		if (v < 0)
			value = 0;
		else
			value = v;

		kind = k;
	}
	
	public int getValue()
	{
		return value;
	}
}
