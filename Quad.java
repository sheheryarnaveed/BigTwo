import java.util.ArrayList;

/**
 * This class is used for representing the 'Quad' combination Hand in BigTwo Game
 * 
 * @author 3035493672
 */

public class Quad extends Hand{
	
	/**
	 * Creates and returns an instance of Quad class
	 * 
	 * @param player CardGamePlayer who plays this hand
	 * 
	 * @param cards CardList which forms the combination to produce this hand
	 */
	Quad(CardGamePlayer player, CardList cards) {
		super(player, cards);
		settypeID(4);
	}
	
	/**
	 * It returns whether the cards form a valid Quad
	 * 
	 * @return true the selected cards make up a valid Quad
	 * 		   false the selected cards do not make up a valid Quad
	 */
	@Override
	boolean isValid(){
		if(size()==5) {
			ArrayList<Integer> numR1 = new ArrayList<Integer>();
			ArrayList<Integer> numR2 = new ArrayList<Integer>();
			int r2 = -1, r1 = (getCard(0).rank+11)%13;
			for(int i = 1 ; i< size(); i++) {
				if((getCard(i).rank+11)%13 != r1) {
					r2 = (getCard(i).rank+11)%13;
					break;
				}
			}
			if(r2 == -1) return false;
			
			for(int i = 0 ; i< size(); i++) {
				if((getCard(i).rank+11)%13 == r1) {
					numR1.add((getCard(i).rank+11)%13);
				}
				else if((getCard(i).rank+11)%13 == r2) {
					numR2.add((getCard(i).rank+11)%13);
				}
			}
			if((numR1.size() == 1 && numR2.size() == 4) || (numR2.size() == 1 && numR1.size() == 4)) {
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
	 * It overrides the superclass's getTopCard() method if hand is a Quad and returns the top card of the Quad hand.
	 * 
	 * @return the top card of this Quad hand.
	 */
	@Override
	public Card getTopCard() {
		sort();
		if(getCard(3).rank != getCard(4).rank) {
			return getCard(3);
		}
		else {
			return getCard(4);
		}
	}
	
	/**
	 * It returns a string value specifying the type of combination.
	 * 
	 * @return a string specifying the name of the combination that is "Quad" in this case
	 */
	@Override
	String getType(){
		return "Quad";
	}
}
