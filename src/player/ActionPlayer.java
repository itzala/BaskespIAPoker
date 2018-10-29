package player;

public class ActionPlayer {
	private ActionKind kind;
	private int value;
	
	public ActionPlayer(int v, int last_bet, int small_blind, int big_blind)
	{
		if (v == 0)
		{
			if (last_bet == -1)
				kind = ActionKind.CHECK;
			else if (last_bet != -1)
				kind = ActionKind.FOLD;
		}
		else if (v == last_bet)
			kind = ActionKind.CALL;
		else if (v > last_bet)
		{
			if (v == small_blind)
				kind = ActionKind.SMALL_BLIND;
			else if (v == big_blind)
				kind = ActionKind.BIG_BLIND;
			else
				kind = ActionKind.RAISE;
		}
			
			
		
		if (v < 0)
			value = 0;
		else
			value = v;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public ActionKind getKind()
	{
		return kind;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ActionPlayer [ Type = ");
		builder.append(kind);
		builder.append(", Valeur misee = ");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
	
	
}
