import java.util.ArrayList;

/**
 * This class is used to model a BigTwo card game
 * 
 * @author 3035493672
 */

public class BigTwo implements CardGame{

	//defining private variables
	/*
	 * This variable contains the deck in use
	 */
	private Deck deck;
	
	/*
	 * This ArrayList contains the list of players in the game
	 */
	private ArrayList<CardGamePlayer> playerList;
	
	/*
	 * This ArrayList contains the list of hands on the table played in the game
	 */
	private ArrayList<Hand> handsOnTable;
	
	/*
	 * This variable contains index value of the current player
	 */
	private int currentIdx;

	
	// This variable is for the BigTwo Game's table GUI
	private BigTwoTable table;
	
	/*
	 * This variable contains the player who made the last move
	 */
	private CardGamePlayer lastTurnPlayer;
	
	/**
	 * Constructor which creates an instance of BigTwo class and returns it
	 * 
	 */
	BigTwo(){
		playerList = new ArrayList<CardGamePlayer>();
		handsOnTable = new ArrayList<Hand>();
		for(int i = 0; i<4; i++) {
			CardGamePlayer player = new CardGamePlayer();
			if(i == 0) player.setName("Henry");
			else if(i == 1) player.setName("Bill");
			else if(i == 2) player.setName("Jacky");
			else { player.setName("Sam"); }
			playerList.add(player);
		}
		table = new BigTwoTable(this);
	}
	
	/**
	 * It creates a BigTwo game and then creates a deck and shuffles it and then starts it
	 * 
	 * @param args which are the arguments, if any, supplied from the command line
	 */
	public static void main(String[] args) {
		BigTwo Game = new BigTwo();
		Game.deck = new BigTwoDeck();
		Game.deck.shuffle();
		Game.start((BigTwoDeck)Game.deck);
	}
	
	/**
	 * Returns the deck in use in the game
	 * 
	 * @return Returns the deck in use in the game
	 */
	public Deck getDeck() {
		return deck;
	};
	
	
	/**
	 * Returns the numbers of players
	 * 
	 * @return size of the given playerList
	 */
	@Override
	public int getNumOfPlayers() {
		// TODO Auto-generated method stub
		return playerList.size();
	}
	
	/**
	 * Returns the list of players currently in the game
	 * 
	 * @return Returns an ArrayList of players playing the game
	 */
	public ArrayList<CardGamePlayer> getPlayerList(){
		return playerList;
	};
	
	/**
	 * Returns the player who did the last turn
	 * 
	 * @return player who played the last hand
	 */
	public CardGamePlayer getLastPlayed(){
		return lastTurnPlayer;
	}
	
	/**
	 * Returns the list of hands played on table in the game
	 * 
	 * @return Returns an ArrayList of the hands played on table in the game
	 */
	public ArrayList<Hand> getHandsOnTable(){
		return handsOnTable;
	};
	
	/**
	 * Returns the index value of the current player
	 * 
	 * @return Returns an integer value denoting the index value of the current player
	 */
	public int getCurrentIdx() {
		return currentIdx;
	};
	
	/**
	 * Starts the game by distributing the shuffled deck of cards between different players and then implements BigTwo Game Logics/rules
	 * 
	 * @param deck which is a shuffled  deck of BigTwo Game
	 */
	public void start(Deck deck) {
		currentIdx = -1;
		
		handsOnTable = new ArrayList<Hand>();
		for(int i=0; i<playerList.size(); i++) {
			playerList.get(i).removeAllCards();
		}
		
		//distributing the cards among players
		for(int i=13; i>0; i--) {
			for(int k=0; k<playerList.size(); k++) {
				playerList.get(k).addCard(deck.getCard(0));
				deck.removeCard(0);
			}
		}
		
		//checking who plays the first hand
		Card diamondT = new Card(0,2); // 3 of Diamond
		for(int k=0; k<playerList.size(); k++) {
			playerList.get(k).sortCardsInHand();
			if(playerList.get(k).getCardsInHand().contains(diamondT)){
				currentIdx = k;
			}
		}
		
		
		table.setActivePlayer(currentIdx);//setting the index of the active player
		lastTurnPlayer = null;
		table.reset();
		table.repaint();
		table.printMsg(this.getPlayerList().get(this.getCurrentIdx()).getName()+"'s turn:");

	};
	
	
	/**
	 * Returns a boolean value indicating whether the game has finished or not.
	 * 
	 * @return true denotes that game has ended 
	 * 			false denotes that the game has not yet ended
	 */
	public boolean endOfGame(){
		for(int i=0; i<playerList.size(); i++){
			if(playerList.get(i).getNumOfCards()==0)
				return true;
		}
		return false;
	}
	
	
	
	/**
	 * Makes the move made by the player
	 * 
	 * @param playerID - The id of the player who makes the move
	 * @param cardIdx - The list containing the indices of the cards selected by the player
	 */
	@Override
	public void makeMove(int playerID, int[] cardIdx) {
		// TODO Auto-generated method stub
		checkMove(playerID, cardIdx);
	}
	
	
	/**
	 * Method for checking a move made by a player.
	 * 
	 * @param playerID - the player Id who is making the move 
	 * @param cardIdx - the indices of the cards played by the player
	 */
	@Override
	public void checkMove(int playerID, int[] cardIdx) {
		// TODO Auto-generated method stub
		if(lastTurnPlayer == null){//for dealing with the first turn of the game
			CardList selectedCards = playerList.get(playerID).play(cardIdx);
			
			
			Hand playedHand = composeHand(playerList.get(playerID), selectedCards);
			if(playedHand == null){
				if(selectedCards != null) {
					table.printMsg("Invalid Move: " + selectedCards.toString());
					table.InvalidTurn();
				}
				else {
					table.printMsg("Invalid Move: {pass}");
					table.InvalidTurn();
				}
				return;
			}
			else if(!selectedCards.contains(new Card(0,2))){
				table.printMsg("Invalid Move: " + selectedCards.toString());
				table.InvalidTurn();
				return;
			}
			else{
				table.printMsg("{"+playedHand.getType()+"}"+playedHand.toString());
			}
			
			++currentIdx; 
			currentIdx = currentIdx % 4; 
			
			lastTurnPlayer = playerList.get(playerID);
			table.setActivePlayer(currentIdx%4);
			playerList.get(playerID).removeCards(selectedCards);
			handsOnTable.add(playedHand);
			table.resetSelected();
			table.nextTurn();
			table.repaint();
			table.printMsg(this.getPlayerList().get(this.getCurrentIdx()).getName()+"'s turn:");
			
			return;
		}
		else if(cardIdx == null){ //player passes the turn
			
			if(lastTurnPlayer != playerList.get(playerID)) {
				table.printMsg("{pass}");
				ValidMove();//need to check if the game has ended and if not the turn needs to be passed to the next player
			}
			else {
				table.printMsg("Invalid Move: {pass}");
				table.InvalidTurn();
			}
		}
		else {//for any other moves as the game continues
			CardList PlayerCards = playerList.get(playerID).play(cardIdx);
			Hand playedHand = composeHand(playerList.get(playerID), PlayerCards);
			if(playedHand == null){ // no hand composed so not a valid move
				table.printMsg("Invalid Move: " + PlayerCards.toString());
				table.InvalidTurn();
			}
			else if(cardIdx.length != handsOnTable.get(handsOnTable.size()-1).size() && lastTurnPlayer != playerList.get(playerID)){//the current player must play cards equal to num of cards on table unless he is the player who made the last move
				table.printMsg("Invalid Move: " + PlayerCards.toString());
				table.InvalidTurn();
			}
			else if(lastTurnPlayer == playerList.get(playerID) || playedHand.beats(handsOnTable.get(handsOnTable.size()-1))){//either the same player replaces the last move or the new player's hand beats the given hand on table
				table.printMsg("{"+playedHand.getType()+"}"+playedHand.toString());
				handsOnTable.add(playedHand);
				playerList.get(playerID).removeCards(playedHand);
				lastTurnPlayer = playerList.get(playerID);
				ValidMove();
			}
			else {
				table.printMsg("Invalid Move: " + PlayerCards.toString());
				table.InvalidTurn();
			}
			
			
		}
		
	}
	
	
	/*
	 * It checks if game has ended and if it does then it prints the final results together with the cards.
	 * In case the game has not been ended yet, it increments the currentIdx passing the turn to next player and repaints the table
	 * 
	 */
	private void ValidMove() {
		if(endOfGame()){
			table.disable();
			table.repaint();
			table.printMsg("Game ends");
			String message = "";
			for(int i =0; i<playerList.size(); i++) {
				CardGamePlayer temp = playerList.get(i);
				if(temp.getNumOfCards()==0) {
					message = " wins the game.";
				}
				else {
					message = " has " + temp.getNumOfCards() + " cards in hand.";
				}
				table.printMsg(temp.getName()+message);
			}
		}else{
			currentIdx++;
			currentIdx = currentIdx % 4; 
			table.setActivePlayer(currentIdx%4);
			table.nextTurn();
			table.printMsg(this.getPlayerList().get(this.getCurrentIdx()).getName()+"'s turn:");
			table.resetSelected();
			table.repaint();
		}
	}
	
	
	
	/**
	 * It forms and returns a valid hand from the given cards supplied as the second argument selected by the player or returns null if no valid hand could be formed
	 * 
	 * @param cards CardList cards that are selected by the player
	 * @param player CardGamePlayer who selects the given list of cards
	 * @return Hand with a valid combination consisting of either of the following: StraightFlush, Quad, FullHouse, Flush, Straight, Triple, Pair, Single
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		if(cards == null) return null;
		Hand hand = null;
		if(cards.size() == 1) {
			hand = new Single(player, cards);
		}
		else if(cards.size() == 2) {
			hand = new Pair(player, cards);
		}
		else if(cards.size() == 3) {
			hand = new Triple(player, cards);
		}
		else if(cards.size() == 5) {
			hand = new StraightFlush(player, cards);
			if(hand.isValid()) return hand;
			hand = new Quad(player, cards);
			if(hand.isValid()) return hand;
			hand = new FullHouse(player, cards);
			if(hand.isValid()) return hand;
			hand = new Flush(player, cards);
			if(hand.isValid()) return hand;
			hand = new Straight(player, cards);
			if(hand.isValid()) return hand;
		}
		if(hand != null) {
			if(hand.isValid()) {
				return hand;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	

}
