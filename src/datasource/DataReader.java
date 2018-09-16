package datasource;

import card.Card;
import card.ValueCard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;

public class DataReader {
	private ArrayList<Map<String, String>> data;
	private Gson parser;
	
	public DataReader()
	{
		this.data = null;
		parser = new Gson();
	}
	
	public ArrayList<Map<String, String>> getData() {
		return data;
	}

	public void setData(ArrayList<Map<String, String>> data) {
		this.data = data;
	}

	public Gson getParser() {
		return parser;
	}

	public void setParser(Gson parser) {
		this.parser = parser;
	}
	
	public ArrayList<Card> getCards()
	{
		ArrayList<Card> cards = new ArrayList<Card>();
		
		for (Iterator<Map<String, String>> iterator = data.iterator(); iterator.hasNext();) {
			Map<String, String> data = (Map<String, String>) iterator.next();
			int index_value = Integer.parseInt(data.get("value"));
			ValueCard value = ValueCard.values()[index_value];
			data.put("value", value.name());
			cards.add(parser.fromJson(data.toString(), Card.class));
		}
		
		return cards;
	}
}