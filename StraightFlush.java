import java.util.ArrayList;
/**
 * This class is used for representing the 'Straight Flush' combination Hand in BigTwo Game
 * 
 * @author 3035493672
 */
public class StraightFlush extends Hand{
	
	/**
	 * Creates and returns an instance of Straight Flush class
	 * 
	 * @param player CardGamePlayer who plays this hand
	 * 
	 * @param cards CardList which forms the combination to produce this hand
	 */
	StraightFlush(CardGamePlayer player, CardList cards) {
		super(player, cards);
		settypeID(5);
	}
	
	/**
	 * It returns whether the cards form a valid Straight Flush
	 * 
	 * @return true the selected cards make up a valid Straight Flush
	 * 		   false the selected cards do not make up a valid Straight Flush
	 */
	@Override
	boolean isValid(){
		if(size()==5) {
			int suite = getCard(0).suit;
			for(int i = 1 ; i< size(); i++) {
				if(getCard(i).suit != suite) {
					return false;
				}
			}
			
			sort();
			int currRank = (getCard(0).rank +11)%13;
			for(int i=0; i<size(); i++) {
				if((getCard(i).rank+11)%13 != currRank) return false;
				currRank++;
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * It returns a string value specifying the type of combination.
	 * 
	 * @return a string specifying the name of the combination that is "Straight Flush" in this case
	 */
	@Override
	String getType(){
		return "Straight Flush";
	}
}
