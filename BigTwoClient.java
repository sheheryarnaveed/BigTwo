import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * This class is used to model a Big Two card game with a networked connection over the internet
 * 
 * @author 3035493672
 */
public class BigTwoClient implements CardGame, NetworkGame {
	
	private int numOfPlayers;
	private Deck deck;
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable;
	private int playerID;
	private String playerName;
	private String serverIP; 
	private int serverPort; 
	private Socket sock;
	private ObjectOutputStream oos;
	private int currentIdx;
	private BigTwoTable table;
	
	private CardGamePlayer lastTurnPlayer; //the player who played the last hand
	private boolean gameStarted = false;
	
	
	
	
	/**
	 * It creates a BigTwo game and then creates a deck and shuffles it and then starts it
	 * 
	 * @param args any arguments supplied from the command line
	 */
	public static void main(String[] args){
		BigTwoClient game = new BigTwoClient();
	}
	
	/**
	 * Creates and returns an instance of the BigTwoClient class.
	 */
	BigTwoClient(){
		handsOnTable = new ArrayList<Hand>();
		playerList = new  ArrayList<CardGamePlayer>();
		for(int i=0;i<4;i++){ //adding four players to player list
			CardGamePlayer Player = new CardGamePlayer();
			Player.setName("Waiting for other layers to join...");
			playerList.add(Player);
		}
		this.numOfPlayers = this.playerList.size();
		this.currentIdx = -1;
		table = new BigTwoTable(this);
		table.disable();
		this.makeConnection();
		
		
	}
	
	
	/**
	 * Returns the number of players
	 * 
	 * @return size of the playerList
	 */
	@Override
	public int getNumOfPlayers() {
		// TODO Auto-generated method stub
		return this.numOfPlayers;
	}
	
	/**
	 * Returns the deck being used in the game
	 * 
	 * @return the deck being used in the game
	 */
	public Deck getDeck(){ 
		return deck;
	}
	
	/**
	 * Returns the list of players currently playing the game
	 * 
	 * @return an ArrayList of the list of players currently playing the game
	 */
	public ArrayList<CardGamePlayer> getPlayerList(){
		return playerList;
	}
	
	/**
	 * Returns the list of hands played in the game
	 * 
	 * @return an ArrayList of the list of hands played in the game
	 */
	public ArrayList<Hand> getHandsOnTable(){ 
		return handsOnTable;
	}
	
	/**
	 * A method for getting the index of the player for the current turn
	 * 
	 * @return an integer value representing the index of the current player
	 */
	public int getCurrentIdx(){
		return currentIdx;
	}

	/**
	 * Starts the game with a shuffled deck of Big Two cards supplied as an argument.
	 * Implements the Big Two game logics.
	 * 
	 * @param deck - a shuffled deck of Big Two cards
	 * 
	 */
	public void start(Deck deck){ 
		gameStarted = true;
		handsOnTable = new ArrayList<Hand>();
		for(int i=0; i<playerList.size(); i++) {
			playerList.get(i++).removeAllCards();
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
		
		table.clearMsgArea();
		table.printMsg("New game has started!");
		table.printMsg(this.getPlayerList().get(this.getCurrentIdx()).getName()+"'s turn:");
		table.setActivePlayer(currentIdx);
		lastTurnPlayer = null;
		table.repaint();
	}

	/**
	 * Makes a move by the player.
	 * 
	 * @param playerID - the playerID of the player who makes the move
	 * @param cardIdx - the list of the indices of the cards selected by the player
	 */
	@Override
	public void makeMove(int playerID, int[] cardIdx) {
		// TODO Auto-generated method stub
		checkMove(playerID, cardIdx);
		try{
			oos.writeObject(new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx));
		}
		catch(Exception e){
			e.printStackTrace();
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
			table.resetSelected();
			table.repaint();
			table.printEndGame();
			handsOnTable.clear();
			for(int i=0; i<4; i++) {
				playerList.get(i).removeAllCards();
			}
			
			sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
			
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
	 * Returns a boolean value indicating whether the game has finished or not.
	 * 
	 * @return true denotes that game has ended 
	 * 		   false denotes that the game has not yet ended
	 */
	public boolean endOfGame(){
		for(int i=0; i<playerList.size(); i++){
			if(playerList.get(i).getNumOfCards()==0)
				return true;
		}
		return false;
	}
	
	
	/**
	 * Returns the CardGamePlayer who did the last turn
	 * 
	 * @return lastPlayer
	 */
	public CardGamePlayer getLastPlayed(){
		return lastTurnPlayer;
	}

	/**
	 * Returns the playerID of the current CardGamePlayer
	 * 
	 * @return playerID
	 */
	@Override
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * Sets the playerID of the current CardGamePlayer
	 * 
	 * @params playerID
	 */
	@Override
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
		playerList.get(playerID).setName(playerName);
	}

	/**
	 * Returns the name of the current CardGamePlayer
	 * 
	 * @return playerName
	 */
	@Override
	public String getPlayerName() { 
		return playerName;	
	}

	/**
	 * Sets the name of the current CardGamePlayer
	 * 
	 * @params playerName
	 */
	@Override
	public void setPlayerName(String playerName) {
		this.playerName = playerName; 
		playerList.get(playerID).setName(playerName);
	}

	/**
	 * Returns the serverIP of the game
	 * 
	 * @return serverIP
	 */
	@Override
	public String getServerIP() { 
		return serverIP; 
	}

	/**
	 * Sets the serverIP of the game
	 * 
	 * @params serverIP
	 */
	@Override
	public void setServerIP(String serverIP) { 
		this.serverIP = serverIP; 
	}

	/**
	 * Returns the server port of the game
	 * 
	 * @return serverPort
	 */
	@Override
	public int getServerPort() { 
		return serverPort;
	}

	/**
	 * Sets the server port of the game
	 * 
	 * @params serverPort
	 */
	@Override
	public void setServerPort(int serverPort) {
		// TODO Auto-generated method stub
		this.serverPort = serverPort;
	}
	
	
	/**
	 * Returns a boolean value to check if the server connection persists
	 * 
	 * @return true-connected
	 * 		   false-disconnected
	 */
	public boolean isConnected(){
		if (sock == null) {
			return false; 
		}
		return true;
	}
	

	/**
	 * Creates a server connection
	 */
	@Override
	public void makeConnection() {
		try{
			sock = new Socket(serverIP, serverPort);
			oos = new ObjectOutputStream(sock.getOutputStream());
			Thread thread = new Thread(new ServerHandler());
			thread.start();
			oos.writeObject(new CardGameMessage(CardGameMessage.JOIN, -1, playerName));
			oos.writeObject(new CardGameMessage(CardGameMessage.READY, -1, null));
		}catch (Exception e){
			e.printStackTrace(); 
		}
	}
	
	
	
	/**
	 * Sending a GameMessage message
	 * 
	 * @params message
	 */
	@Override
	public void sendMessage(GameMessage message) {
		// TODO Auto-generated method stub
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Sending a GameMessage message that can of various types depending on the message such as
	 * join, full start, move, ready, quit and msg
	 * 
	 * @params message - GameMessage
	 */
	@Override
	public void parseMessage(GameMessage message) {
		// TODO Auto-generated method stub
		String data = "";
		if(message.getType() == CardGameMessage.PLAYER_LIST){
			setPlayerID(message.getPlayerID());
			String[] names = (String[]) message.getData();
			for(int i=0; i < names.length; i++){
				if(names[i] != null && i != playerID) {
					playerList.get(i).setName(names[i]);
				}
			}
			table.repaint();
		}	
		else if(message.getType() == CardGameMessage.JOIN) {
			if(message.getPlayerID() != playerID){
				data = (String)message.getData();
				playerList.get(message.getPlayerID()).setName(data);
				table.printMsg(data+" has joined the game, say hello!");
			}
			table.repaint();
		}
		else if(message.getType() == CardGameMessage.FULL) {
			table.printMsg("Server is full - cannot join the game at the moment!");
		}
		else if(message.getType() == CardGameMessage.QUIT) {
			data = playerList.get(message.getPlayerID()).getName();
			table.printMsg(data + " has left the game.");
			playerList.get(message.getPlayerID()).setName("");
			if(gameStarted){
				table.disable();
				try {
					oos.writeObject(new CardGameMessage(CardGameMessage.READY, -1, null));
				} catch (IOException e) { e.printStackTrace(); }
			}
		}
		else if(message.getType() == CardGameMessage.READY) {
			if(message.getPlayerID() != playerID){
				data = playerList.get(message.getPlayerID()).getName();
				table.printMsg(data + " is ready.");
			}
		}
		else if(message.getType() == CardGameMessage.START) {
			table.enable();
			start((BigTwoDeck)message.getData());
		}
		else if(message.getType() == CardGameMessage.MOVE) {
			if(message.getPlayerID() != playerID) {
				checkMove(message.getPlayerID(), (int[])message.getData());
			}
		}
		else if(message.getType() == CardGameMessage.MSG) {
			table.printChatMsg((String)message.getData());
		}
		else {
			System.out.println("Received an unknown type message!");
			System.out.println("Type: " + message.getType());
			System.out.println("Data: " + message.getData());
			System.out.println("PlayerID: " + message.getPlayerID());
		}
	}
	
	
	
	/**
	 * An inner class that implements the Runnable interface. 
	 * 
	 */
	class ServerHandler implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				ObjectInputStream reader = new ObjectInputStream(sock.getInputStream());
				CardGameMessage msg;
				while (!sock.isClosed()) {
					if ((msg = (CardGameMessage) reader.readObject()) != null) {
						parseMessage(msg);
					}
				} // close while
				reader.close();
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		
	}
	
}
