/**
 * This class is used for representing the 'Triple' combination Hand in BigTwo Game
 * 
 * @author 3035493672
 */
public class Triple extends Hand{
	
	/**
	 * Creates and returns an instance of Triple class
	 * 
	 * @param player CardGamePlayer who plays this hand
	 * 
	 * @param cards CardList which forms the combination to produce this hand
	 */
	Triple(CardGamePlayer player, CardList cards) {
		super(player, cards);
		settypeID(0);
	}
	
	/**
	 * It returns whether the cards form a valid Triple
	 * 
	 * @return true the selected cards make up a valid Triple
	 * 		   false the selected cards do not make up a valid Triple
	 */
	@Override
	boolean isValid(){
		int rank= getCard(0).rank;
		for(int i=1; i<size(); i++){
			if(getCard(i).rank != rank) {
				return false;
			}
		}
		if(size()==3) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * It returns a string value specifying the type of combination.
	 * 
	 * @return a string specifying the name of the combination that is "Triple" in this case
	 */
	@Override
	String getType(){
		return "Triple";
	}
}
