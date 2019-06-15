/**
 * This class is used for representing the 'Single' combination Hand in BigTwo Game
 * 
 * @author 3035493672
 */
public class Single extends Hand{
	
	/**
	 * Creates and returns an instance of Single class
	 * 
	 * @param player CardGamePlayer who plays this hand
	 * 
	 * @param cards CardList which forms the combination to produce this hand
	 */
	Single(CardGamePlayer player, CardList cards) {
		super(player, cards);
		settypeID(0);
	}
	
	/**
	 * It returns whether the cards form a valid Single
	 * 
	 * @return true the selected cards make up a valid Single
	 * 		   false the selected cards do not make up a valid Single
	 */
	@Override
	boolean isValid(){
		if(size()==1) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * It returns a string value specifying the type of combination.
	 * 
	 * @return a string specifying the name of the combination that is "Single" in this case
	 */
	@Override
	String getType(){
		return "Single";
	}
	
}
