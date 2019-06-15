/**
 * This class is used for representing a card in BigTwoCard game 
 * 
 * @author 3035493672
 */
public class BigTwoCard extends Card{
	
	/**
	 * Constructor which creates an instance of BigTwoCard class and returns it
	 * 
	 * an int value between 0 and 3 representing the suit of a card:
	 *            <p>
	 *            0 = Diamond, 1 = Club, 2 = Heart, 3 = Spade
	 * 
	 * @param rank It is an integer value between 0 and 12 that represents the rank of the card
	 *            <p>
	 *            0 = 'A', 1 = '2', 2 = '3', ..., 8 = '9', 9 = '0', 10 = 'J', 11
	 *            = 'Q', 12 = 'K'
	 */
	public BigTwoCard(int suit, int rank) {
		super(suit, rank);
	}
	
	
	/**
	 * Compares this card with the specified card for order in a way in which BigTwo Game is played
	 * 
	 * @param card
	 *            the card to be compared
	 * @return a negative integer, zero, or a positive integer as this card is
	 *         less than, equal to, or greater than the specified card
	 */
	@Override
	public int compareTo(Card card) {
		int BigTwoRank = (getRank()+11)%13;//for correct comparison of ranks
		int BigTwoRankCard = (card.rank+11)%13;
		if (BigTwoRank > BigTwoRankCard) {
			return 1;
		} else if (BigTwoRank < BigTwoRankCard) {
			return -1;
		} else if (this.suit > card.suit) {
			return 1;
		} else if (this.suit < card.suit) {
			return -1;
		} else {
			return 0;
		}
	}
	
	
}
