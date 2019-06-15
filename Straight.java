/**
 * This class is used for representing the 'Straight' combination Hand in BigTwo Game
 * 
 * @author 3035493672
 */
public class Straight extends Hand{
	
	/**
	 * Creates and returns an instance of Straight class
	 * 
	 * @param player CardGamePlayer who plays this hand
	 * 
	 * @param cards CardList which forms the combination to produce this hand
	 */
	Straight(CardGamePlayer player, CardList cards) {
		super(player, cards);
		settypeID(1);
	}
	
	/**
	 * It returns whether the cards form a valid Straight
	 * 
	 * @return true the selected cards make up a valid Straight
	 * 		   false the selected cards do not make up a valid Straight
	 */
	@Override
	boolean isValid(){
		if(size()==5) {
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
	 * @return a string specifying the name of the combination that is "Straight" in this case
	 */
	@Override
	String getType(){
		return "Straight";
	}
}
