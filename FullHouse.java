import java.util.ArrayList;

/**
 * This class is used for representing the 'FullHouse' combination Hand in BigTwo Game
 * 
 * @author 3035493672
 */

public class FullHouse extends Hand{
	
	/**
	 * Creates and returns an instance of FullHouse class
	 * 
	 * @param player CardGamePlayer who plays this hand
	 * 
	 * @param cards CardList which forms the combination to produce this hand
	 */
	FullHouse(CardGamePlayer player, CardList cards) {
		super(player, cards);
		settypeID(3);
	}
	
	
	/**
	 * It returns whether the cards form a valid FullHouse
	 * 
	 * @return true the selected cards make up a valid FullHouse
	 * 		   false the selected cards do not make up a valid FullHouse
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
			if((numR1.size() == 2 && numR2.size() == 3) || (numR2.size() == 2 && numR1.size() == 3)) {
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
	 * It overrides the superclass's getTopCard() method if hand is a FullHouse and returns the top card of the FullHouse hand.
	 * 
	 * @return the top card of this FullHouse hand.
	 */
	@Override
	public Card getTopCard() {
		sort();
		if(getCard(1).rank != getCard(2).rank) {
			return getCard(4);
		}
		else {
			return getCard(2);
		}
	}
	
	/**
	 * It returns a string value specifying the type of combination.
	 * 
	 * @return a string specifying the name of the combination that is "FullHouse" in this case
	 */
	@Override
	String getType(){
		return "FullHouse";
	}
}
