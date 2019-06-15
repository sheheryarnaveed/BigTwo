/**
 * This class is used for representing a model of different combinations to form a hand in BigTwo Game 
 * It is not possible to instantiate this class because it is abstract
 * 
 * @author 3035493672
 */
public abstract class Hand extends CardList{
	//defining private variables
	
	/**
	 * This is a CardGamePlayer variable to represent the game player
	 */
	private CardGamePlayer player;
	
	/**
	 * This variable is used to represent the type of hand being formed
	 * 			<p>
	 * 				0 = Single or Pair or Triple, 1 = Straight, 2 = flush, 3 = FullHouse, 4 = Quad, 5 = StraightFlush
	 */
	private int typeId;
	
	
	/**
	 * Constructor which can be called by the subclasses. An instance of this class cannot be created as it is abstract
	 * 
	 * @param player CardGamePlayer who plays this hand
	 * 
	 * @param cards CardList which forms the combination to produce this hand
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		this.player = player;
		for(int i=0; i<cards.size(); i++) {
			addCard(cards.getCard(i));
		}
	}
	
	/**
	 * Returns the CardGamePlayer of this current hand
	 * 
	 * @return the CardGamePlayer of this current hand
	 */
	public CardGamePlayer getPlayer() {
		return this.player;
	}
	
	/**
	 * Returns the top card of this current hand
	 * 
	 * @return the top card of this current hand
	 */
	public Card getTopCard() {
		int index = 0;
		for(int i=1; i<size(); i++) {
			if(getCard(i).compareTo(getCard(index))==1) {
				index = i;
			}
		}
		return getCard(index);
	}
	
	/**
	 * Returns a boolean value specifying whether the given hand beats the current hand
	 * 
	 * @param hand against which the current hand is to be checked
	 * 
	 * @return true if the current hand can beat the hand provided as the argument
	 * 			false if the current hand cannot beat the hand provided as the argument
	 */
	public boolean beats(Hand hand) {
		if(typeId == 0 && hand.typeId == 0) {//single, double and triple(the one having the highest rank betas the other)
			if(hand.getTopCard().compareTo(this.getTopCard()) == -1){
				return true;	
			}else{
				return false;
			}
		}
		else if(typeId != 0 && hand.typeId != 0) {
			if(this.typeId > hand.gettypeID()){
				return true;
			}else if(this.typeId < hand.gettypeID()){
				return false;
			}else if(hand.getTopCard().compareTo(this.getTopCard()) == -1){
				return true;	
			}else{
				return false;
			}
		}
		return false;//cannot compare the zero id samples with non zero id samples
	}
	
	/**
	 * Sets a particular id for each combination for easier comparison
	 * 
	 * @param id an integer value between 0 and 5
	 * 			<p>
	 * 			0 = Single or Pair or Triple, 1 = Straight, 2 = Flush, 3 = FullHouse, 4 = Quad, 5 = StraightFlush
	 */
	public void settypeID(int id) {
		typeId = id;
	}
	
	/**
	 * Gets a particular id for each combination that was set earlier
	 * 
	 * @return an integer value between 0 and 5
	 * 			<p>
	 * 			0 = Single or Pair or Triple, 1 = Straight, 2 = Flush, 3 = FullHouse, 4 = Quad, 5 = StraightFlush
	 */
	public int gettypeID() {
		return typeId;
	}
	
	/**
	 * It will get overridden by the subclasses as it is an abstract method and returns a string value specifying the type of combination.
	 * 
	 * @return a string specifying the type of the combination
	 */
	abstract String getType();
	
	/**
	 * It will get overridden by the subclasses as it is an abstract method and returns whether the cards form a valid combination out of the given valid possible hands
	 * 
	 * @return true the selected cards make up a valid combination
	 * 		   false the selected cards do not make up a valid combination
	 */
	abstract boolean isValid();
	
}
