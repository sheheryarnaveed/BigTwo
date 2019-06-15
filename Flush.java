/**
 * This class is used for representing the 'Flush' combination Hand in BigTwo Game
 * 
 * @author 3035493672
 */
public class Flush extends Hand{
	
	/**
	 * Creates and returns an instance of Flush class
	 * 
	 * @param player CardGamePlayer who plays this hand
	 * 
	 * @param cards CardList which forms the combination to produce this hand
	 */
	Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
		settypeID(2);
	}
	
	/**
	 * It returns whether the cards form a valid Flush
	 * 
	 * @return true the selected cards make up a valid Flush
	 * 		   false the selected cards do not make up a valid Flush
	 */
	@Override
	boolean isValid(){
		if(size()==5) {
			sort();
			int currsuit = getCard(0).suit;
			for(int i=1; i<size(); i++) {
				if(getCard(i).suit != currsuit) return false;
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * It returns a boolean value specifying whether the given hand beats the current hand and it is able to override the superclass's method if both of the hand types are flush
	 * 
	 * @param hand against which the current hand is to be checked
	 * 
	 * @return true if the current hand can beat the hand provided as the argument
	 * 			false if the current hand cannot beat the hand provided as the argument
	 */
	@Override
	public boolean beats(Hand hand){
		if(this.gettypeID() != hand.gettypeID()) {
			return super.beats(hand);
		}
		else if(hand.getTopCard().suit < this.getTopCard().suit) {
			return true;
		}
		else if(hand.getTopCard().suit == this.getTopCard().suit) {
			if((hand.getTopCard().rank+11)%13 < (this.getTopCard().rank+11)%13) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	/**
	 * It returns a string value specifying the type of combination.
	 * 
	 * @return a string specifying the name of the combination that is "Flush" in this case
	 */
	@Override
	String getType(){
		return "Flush";
	}
}
