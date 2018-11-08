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
	private final int COUNT_LINE_STAT 	= Card.getNbDifferentValues();
	private final int COUNT_COLUMN_STAT = Card.getNbColors();

	private int coinsVariation;
	
	private void log(String message)
	{
		System.out.println("[ MAIN ] " + message);
	}
	
	public Hand(int index) {
		indexHand = index;
		
		cards = new ArrayList<Card>();
		
		// On ajoute 1 pour gérer les compteurs
		this.log("Initialisation de la main....");		
		statsCards = new int[COUNT_LINE_STAT + 1][COUNT_COLUMN_STAT + 1];
		bestCombinaison = null;
		
		coinsVariation = 0;
		
		this.log("	Initialisation des statistiques....");
		// initialisation des index
		for (int i = 0; i < COUNT_LINE_STAT ; i++) {
			for (int j = 0; j < COUNT_COLUMN_STAT; j++){
				statsCards[i][j] = -1;
			}
		}
		
		// initilisation des compteurs
		for (int i = 0; i < COUNT_LINE_STAT; i++) {	 
			statsCards[i][COUNT_COLUMN_STAT] = 0;
		}
		for (int j = 0; j < COUNT_COLUMN_STAT; j++){
			statsCards[COUNT_LINE_STAT][j] = 0;
		}
	}
	
	public void addCard(Card c) {
		if (! cards.contains(c) && cards.size() < Constantes.NB_MAX_CARDS){
			int indexNewCard = cards.size();			
			c.setIndexCard(indexNewCard);
			cards.add(c);
			this.log("Ajout de la carte n°" + (indexNewCard + 1) + " => " + c);
			/* 		Mise à jour des stats			*/
			
			int line   = c.getIntValue() ;
			int column = c.getColor().ordinal();
			
			statsCards[line][column] = c.getIndexCard();			
			statsCards[COUNT_LINE_STAT][column]++;
			statsCards[line][COUNT_COLUMN_STAT]++;
			
			
			// Gestion du cas de l'AS qui représente aussi le 1
			if (c.getValue() == ValueCard.AS){
				line = ValueCard.ONE.ordinal();
				statsCards[line][column] = c.getIndexCard();
				statsCards[COUNT_LINE_STAT][column]++;
				statsCards[line][COUNT_COLUMN_STAT]++;
			}
			
			this.log("	Mes cartes : " + cards);
			calculateCombinaisons();
		}
	}
	
	public void addCards(List<Card> cardList){
		for (Card card : cardList) {
			this.addCard(card);
		}
	}
	
	public int getNbSuitedCards()
	{
		int nbSuitedCards = 0;
		for (int i = COUNT_COLUMN_STAT; i >= 0; i--){
			if (statsCards[COUNT_LINE_STAT][i] > 0){
				nbSuitedCards  = statsCards[COUNT_LINE_STAT][i];
				break;
			}
		}
		return nbSuitedCards ;
	}
	
	private int getIndexOfFirstCount(int countSearched, boolean inValue, int excludedCount)
	{
		int index = -1;
		
		// on cherche dans les compteurs par valeurs (dernière colonne)
		if (inValue){ 
			for (int i = COUNT_LINE_STAT; i >= 0; i--){
				if (statsCards[i][COUNT_COLUMN_STAT] == countSearched && i != excludedCount){
					index = i;
					break;
				}
			}
		} else {
			// on cherche dans les compteurs par couleurs (dernière ligne)
			for (int i = COUNT_COLUMN_STAT; i >= 0; i--){
				if (statsCards[COUNT_LINE_STAT][i] == countSearched && i != excludedCount){
					index = i;
					break;
				}
			}
		}
		
		return index;
	}
	
	private Card getCardByStatIndex(int line, int column){
		if ((line > 0 && line < COUNT_LINE_STAT)
				&& (column > 0 && column < COUNT_COLUMN_STAT)
				&& statsCards[line][column] != -1){
			return cards.get(statsCards[line][column]);
		}
		return null;
	}
	
	// Renvoie toutes les cartes de même couleur
	private List<Card>  getCardsByLine(int line){
		ArrayList<Card> cardsList = new ArrayList<Card>();
		// Gestion de l'AS
		if (line == ValueCard.ONE.ordinal()){
			line = ValueCard.AS.ordinal();
		}
		for (int i = COUNT_COLUMN_STAT - 1 ; i >= 0; i--){
			Card c = getCardByStatIndex(line, i);
			if (c != null){
				cards.add(c);
			}
		}
		return cardsList;
	}
	
	// Renvoie toutes les cartes de même valeur
	private List<Card>  getCardsByColumn(int column){
		ArrayList<Card> cardsList = new ArrayList<Card>();
		for (int i = COUNT_LINE_STAT - 1; i >= 0; i--){
			Card c = getCardByStatIndex(i, column);
			if (c != null){
				cardsList.add(c);
			}
		}
		return cardsList;
	}
	
	private Combinaison getCombinaisonForPaire(int excludedValue) {		
		int indexPaire = getIndexOfFirstCount(2, true, excludedValue);
		if (indexPaire != -1){
			ArrayList<Card> cardsPaire = new ArrayList<Card>();
			cardsPaire.addAll(getCardsByLine(indexPaire));
			this.log("	PAIRE trouvée");
			return new Combinaison(CombinaisonKind.PAIRE, cardsPaire);
		}
		return null;
	}
	
	private Combinaison getCombinaisonForBrelan() {
		int indexBrelan = getIndexOfFirstCount(3, true, -1);
		if (indexBrelan != -1){
			ArrayList<Card> cardsBrelan = new ArrayList<Card>();
			cardsBrelan.addAll(getCardsByLine(indexBrelan));
			this.log("	BRELAN trouvé");
			return new Combinaison(CombinaisonKind.BRELAN, cardsBrelan);
		}
		return null;
	}
	
	private Combinaison getCombinaisonForCarre() {
		int indexCarre = getIndexOfFirstCount(4, true, -1);
		if (indexCarre != -1){
			ArrayList<Card> cardsCarre = new ArrayList<Card>();
			cardsCarre.addAll(getCardsByLine(indexCarre));
			this.log("	CARRE trouvé");
			return new Combinaison(CombinaisonKind.CARRE, cardsCarre);
		}
		return null;
	}
	
	private Combinaison getCombinaisonForFull() {
		Combinaison brelan = getCombinaisonForBrelan();
		if (brelan != null){
			Combinaison paire = getCombinaisonForPaire(brelan.getBestCard().getIntValue());
			if (paire != null){
				this.log("	FULL trouvé");
				return new Combinaison(CombinaisonKind.FULL, brelan, paire);
			}
		}
		return null;
	}
	
	private Combinaison getCombinaisonForDoublePaire(){
		Combinaison firstPaire = getCombinaisonForBrelan();
		if (firstPaire != null){
			Combinaison secondPaire = getCombinaisonForPaire(firstPaire.getBestCard().getIntValue());
			if (secondPaire != null){
				this.log("	DOUBLE PAIRE trouvée");
				return new Combinaison(CombinaisonKind.DOUBLE_PAIRE, firstPaire, secondPaire);
			}
		}
		return null;
	}
	
	private Combinaison getCombinaisonForColor(){
		int indexColor = getIndexOfFirstCount(5, false, -1);
		if (indexColor != -1){
			ArrayList<Card> cardsColor = new ArrayList<Card>();
			cardsColor.addAll(getCardsByLine(indexColor));
			this.log("	COULEUR trouvée");
			return new Combinaison(CombinaisonKind.COLOR, cardsColor);
		}
		return null;
	}
	
	private Combinaison getCombinaisonForQuinte(){
		ArrayList<Card> cardsQuinte = new ArrayList<Card>();
		for (int i = COUNT_COLUMN_STAT; i >= 0; i--){
			if (statsCards[COUNT_LINE_STAT][i] > 0){
				List<Card> cardsByColumn = getCardsByColumn(i);				
				if (!cardsByColumn.isEmpty()){
					cardsQuinte.add(cardsByColumn.get(0));
				}
			} else {
				cardsQuinte.clear();
			}
		}
		if (cardsQuinte.size() == 5){
			this.log("	QUINTE trouvée");
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
			this.log("	Meilleure carte trouvée => " + bestCard);
			this.log("Liste des hightCard => " + hightCard);
			return new Combinaison(CombinaisonKind.HIGHT_CARD, hightCard);
		}
		return null;
	}
	
	public void calculateCombinaisons() {
		this.log("	Mise à jour des combinaisons....");
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
				Combinaison full = getCombinaisonForFull();
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
									} else {
										Combinaison hightestCard = getHightestCard();
										if (hightestCard != null) {
											currentCombinaison = hightestCard;
											this.log("Mise à jour de la combinaison courante : " + currentCombinaison);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		this.log("	Vérification de la meilleure combinaison....");
		//this.log(currentCombinaison.getKind() + " est meilleure que " + bestCombinaison.getKind() + "?");
		if (currentCombinaison.isStrongerThan(bestCombinaison)){
			this.log("Mise à jour de la meilleure combinaison");
			bestCombinaison = currentCombinaison;
		} else {
			this.log(currentCombinaison + " n'est pas meilleure que " + bestCombinaison);
		}
	}
	
	public Combinaison getBestCombinaison() {
		return bestCombinaison;
	}
	
	public void setCoinsVariation(int variation){
		// permettra de déterminer si c'est une main gagnante (variation positive) ou perdante (variation négative)
		this.log("Mise à jour de la variation des jetons...");
		coinsVariation = variation;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("-------------- Main n°" + indexHand + " ---------\n");
		builder.append("Nombre de carte : " + cards.size() + "\n");
		builder.append("Meilleure combinaison : " + bestCombinaison + "\n");
		builder.append("Variation du nombre de jetons  : " + coinsVariation + "\n");
		builder.append("-------------- /Main ---------\n");
				
		return builder.toString();
	}

	public int getNbCards() {
		return cards.size();
		
	}
}
