/**
 * This class is used for representing a card deck in BigTwoCard game 
 * 
 * @author 3035493672
 */

public class BigTwoDeck extends Deck {
	
	/**
	 * Creates and returns an instance of the BigTwoDeck class.
	 */
	public BigTwoDeck() {
		super();
		initialize();
	}
	
	
	/**
	 * Initialize a deck of BigTwo cards.
	 */
	@Override
	public void initialize() {
		removeAllCards();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				BigTwoCard card = new BigTwoCard(i, j);
				addCard(card);
			}
		}
	}
	
	
	
}
