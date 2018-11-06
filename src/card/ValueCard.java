package card;

import com.google.gson.annotations.SerializedName;

//Valeurs triees par ordre de puissance

public enum ValueCard {
	ONE,
	@SerializedName("2") TWO,
	@SerializedName("3") THREE,
	@SerializedName("4") FOUR,
	@SerializedName("5") FIVE,
	@SerializedName("6") SIX,
	@SerializedName("7") SEVEN,
	@SerializedName("8") EIGHT,
	@SerializedName("9") NINE,
	@SerializedName("10") TEN, 
	JACK, 
	QUEEN, 
	KING, 
	@SerializedName("1") AS
}
