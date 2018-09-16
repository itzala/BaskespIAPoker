import datasource.Adaptator;
import game.Game;

public class Main {

	public static void main(String[] args) {
		
	Game game = new Game();
	Adaptator adaptator = new Adaptator(game);
	adaptator.parseData();
	adaptator.release();
	}

}
