package card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constantes.Constantes;

public class Hand {
	private int indexHand;
	private List<Card> cards;
	
	private Combinaison bestCombinaison;
	private int[][] statsCards;
	private final int COUNT_LINE_STAT 	= Card.getNbColors();	
	private final int COUNT_COLUMN_STAT = Card.getNbDifferentValues();

	private int coinsVariation;
	
	public Hand(int index) {
		indexHand = index;
		
		cards = new ArrayList<Card>();
		
		// On ajoute 1 pour gérer les compteurs
		statsCards = new int[COUNT_LINE_STAT + 1][COUNT_COLUMN_STAT + 1];
		bestCombinaison = null;
		
		coinsVariation = -1;
		
		// initialisation des index
		for (int i = 0; i < COUNT_LINE_STAT ; i++) {
			for (int j = 0; j < COUNT_COLUMN_STAT; j++){
				statsCards[i][j] = -1;
			}
		}
		
		// initilisation des compteurs
		for (int i = 0; i < COUNT_LINE_STAT; i++) {	// pour les 
			statsCards[i][COUNT_COLUMN_STAT] = 0;
		}
		for (int j = 0; j < COUNT_COLUMN_STAT; j++){
			statsCards[COUNT_LINE_STAT][j] = 0;
		}
	}
	
	public void addCard(Card c) {
		if (! cards.contains(c) && cards.size() < Constantes.NB_MAX_CARDS){
			cards.add(c);
			/* 		Mise à jour des stats			*/
			
			int line = c.getColor().ordinal();
			int column = c.getIntValue() ;
			statsCards[line][column] = c.getIndexCard();
			statsCards[COUNT_LINE_STAT][column]++;
			statsCards[line][COUNT_COLUMN_STAT]++;
			
			
			// Gestion du cas de l'AS qui représente aussi le 1
			if (c.getValue() == ValueCard.AS){
				column = ValueCard.ONE.ordinal();
				statsCards[line][column] = c.getIndexCard();
				statsCards[COUNT_LINE_STAT][column]++;
				statsCards[line][COUNT_COLUMN_STAT]++;
			}
		}
	}
	
	public void addCards(List<Card> cardList){
		for (Card card : cardList) {
			this.addCard(card);
		}
	}
	
	private int getIndexOfFirstCount(int countSearched, boolean inValue, int excludedCount)
	{
		int index = -1;
		
		// on cherche dans les compteurs par valeurs
		if (inValue){ 
			for (int i = COUNT_LINE_STAT; i >= 0; i--){
				if (statsCards[COUNT_COLUMN_STAT][i] == countSearched && i != excludedCount){
					index = i;
					break;
				}
			}
		} else {
			// on cherche dans les compteurs par couleurs
			for (int i = COUNT_COLUMN_STAT; i >= 0; i--){
				if (statsCards[i][COUNT_LINE_STAT] == countSearched && i != excludedCount){
					index = i;
					break;
				}
			}
		}
		
		return index;
	}
	
	private Card getCardByStatIndex(int line, int column){
		if ((line > 0 && line <= COUNT_LINE_STAT)
				&& (column > 0 && column <= COUNT_COLUMN_STAT)
				&& statsCards[column][line] != -1){
			return cards.get(statsCards[column][line]);
		}
		return null;
	}
	
	private List<Card>  getCardsByLine(int line){
		ArrayList<Card> cardsList = new ArrayList<Card>();
		for (int i = COUNT_COLUMN_STAT; i >= 0; i--){
			Card c = getCardByStatIndex(line, i);
			if (c != null){
				cards.add(c);
			}
		}
		return cardsList;
	}
	
	private List<Card>  getCardsByColumn(int column){
		ArrayList<Card> cardsList = new ArrayList<Card>();
		for (int i = COUNT_LINE_STAT; i >= 0; i--){
			Card c = getCardByStatIndex(i, column);
			if (c != null){
				cards.add(c);
			}
		}
		return cardsList;
	}
	
	private Combinaison getCombinaisonForPaire(int excludedValue) {
		int indexPaire = getIndexOfFirstCount(2, true, excludedValue);
		if (indexPaire != -1){
			ArrayList<Card> cardsPaire = new ArrayList<Card>();
			cardsPaire.addAll(getCardsByLine(indexPaire));
			return new Combinaison(CombinaisonKind.PAIRE, cardsPaire);
		}
		return null;
	}
	
	private Combinaison getCombinaisonForBrelan() {
		int indexBrelan = getIndexOfFirstCount(3, true, -1);
		if (indexBrelan != -1){
			ArrayList<Card> cardsBrelan = new ArrayList<Card>();
			cardsBrelan.addAll(getCardsByLine(indexBrelan));
			return new Combinaison(CombinaisonKind.BRELAN, cardsBrelan);
		}
		return null;
	}
	
	private Combinaison getCombinaisonForCarre() {
		int indexCarre = getIndexOfFirstCount(4, true, -1);
		if (indexCarre != -1){
			ArrayList<Card> cardsCarre = new ArrayList<Card>();
			cardsCarre.addAll(getCardsByLine(indexCarre));
			return new Combinaison(CombinaisonKind.CARRE, cardsCarre);
		}
		return null;
	}
	
	private Combinaison getCombinaisonForCFull() {
		Combinaison brelan = getCombinaisonForBrelan();
		if (brelan != null){
			Combinaison paire = getCombinaisonForPaire(brelan.getBestCard().getIntValue());
			if (paire != null){
				return new Combinaison(CombinaisonKind.FULL, brelan, paire);
			}
		}
		return null;
	}
	
	private Combinaison getCombinaisonForDoublePaire(){
		Combinaison firtPaire = getCombinaisonForBrelan();
		if (firtPaire != null){
			Combinaison secondPaire = getCombinaisonForPaire(firtPaire.getBestCard().getIntValue());
			if (secondPaire != null){
				return new Combinaison(CombinaisonKind.DOUBLE_PAIRE, firtPaire, secondPaire);
			}
		}
		return null;
	}
	
	private Combinaison getCombinaisonForColor(){
		int indexColor = getIndexOfFirstCount(5, false, -1);
		if (indexColor != -1){
			ArrayList<Card> cardsColor = new ArrayList<Card>();
			cardsColor.addAll(getCardsByLine(indexColor));
			return new Combinaison(CombinaisonKind.COLOR, cardsColor);
		}
		return null;
	}
	
	private Combinaison getCombinaisonForQuinte(){
		ArrayList<Card> cardsQuinte = new ArrayList<Card>();
		for (int i = COUNT_LINE_STAT; i >= 0; i--){
			if (statsCards[COUNT_COLUMN_STAT][i] > 0){
				cardsQuinte.add(getCardsByLine(i).get(0));
			} else {
				cardsQuinte.clear();
			}
		}
		if (cardsQuinte.size() == 5){
			return new Combinaison(CombinaisonKind.QUINTE, cardsQuinte);
		}
		return null;
	}
	
	private Combinaison getHightestCard(){
		Card bestCard = null;
		for (Card card : cards) {
				if (card.isStrongerThan(bestCard)){
					bestCard = card;
				}
		}
		if (bestCard != null){
			ArrayList<Card> hightCard = new ArrayList<Card>();
			hightCard.add(bestCard);
			return new Combinaison(CombinaisonKind.HIGHT_CARD, hightCard);
		}
		return null;
	}
	
	public void calculateCombinaisons() {
		Combinaison quinte = getCombinaisonForQuinte();
		Combinaison color = getCombinaisonForColor();
		Combinaison currentCombinaison = null;
		
		if (quinte != null && color != null){							// si l'on a une couleur et une quinte, on doit déterminer si ce sont les mêmes cartes
			Combinaison quinteFlush = quinte;
			quinteFlush.getCards().retainAll(color.getCards());			// on calcule l'intersection entre les cartes de la couleur et celles de la quinte
			if (quinteFlush.getCards().size() == 5){					// Si on a rien supprimé, on a une quinte flush
				if (quinteFlush.getBestCard().getValue() == ValueCard.AS){		// si la meilleure carte de la quinte flush est un AS, alors c'est une quinte flush royale
					currentCombinaison =  new Combinaison(CombinaisonKind.QUINTE_FLUSH_ROYAL, quinteFlush.getCards());
				} else {
					currentCombinaison =  new Combinaison(CombinaisonKind.QUINTE_FLUSH, quinteFlush.getCards());
				}
			}
		}
		
		if (currentCombinaison == null){
			Combinaison carre = getCombinaisonForCarre();
			if (carre != null){
				currentCombinaison = carre;
			} else {
				Combinaison full = getCombinaisonForCFull();
				if (full != null){
					currentCombinaison = full;
				} else {
					if (color != null){
						currentCombinaison = color;
					} else {
						if (quinte != null){
							currentCombinaison = quinte;
						} else {
							Combinaison brelan = getCombinaisonForBrelan();
							if (brelan != null){
								currentCombinaison = brelan;
							} else {
								Combinaison doublePaire = getCombinaisonForDoublePaire();
								if (doublePaire != null){
									currentCombinaison = doublePaire;
								} else {
									Combinaison paire = getCombinaisonForPaire(-1);
									if (paire != null) {
										currentCombinaison = paire;
									}
								}
							}
						}
					}
				}
			}
		}
		
		if (currentCombinaison.isStrongerThan(bestCombinaison)){
			bestCombinaison = currentCombinaison;
		}
	}
	
	public Combinaison getBestCombinaison() {
		return bestCombinaison;
	}
	
	public void setCoinsVariation(int variation){
		// permettra de déterminer si c'est une main gagnante (variation positive) ou perdante (variation négative)
		coinsVariation = variation;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Hand [Nombre de cartes = ");
		builder.append(cards.size());
		builder.append("/" + Constantes.NB_MAX_CARDS+ " , \nCartes = ");
		builder.append(cards);
		builder.append(",\nMeilleure combinaison = ");
		builder.append(bestCombinaison);
		builder.append(",\n Variation des jetons = ");
		builder.append(coinsVariation);
		builder.append("\n]");
		return builder.toString();
	}
}
