package cs350.poker.model;

/**
 * Represents a single immutable playing card in a standard 52-card deck.
 *
 * <p>
 * Each card is identified by a suit (1=Clubs, 2=Diamonds, 3=Hearts, 4=Spades)
 * and a face value (1=Ace, 2=2, ..., 11=Jack, 12=Queen, 13=King). Cards are
 * immutable once constructed; no mutator methods are provided.
 * </p>
 *
 * <p>
 * The rank of a card is computed so that Aces are highest and Deuces lowest.
 * Rank formula: if Ace, rank = 12*4 + suit; otherwise rank = (faceValue - 2)*4
 * + suit. This yields a unique rank for every card in the deck (1..52).
 * </p>
 *
 * @author Poker Demo
 * @version 1.0
 */
public class Card implements Comparable<Card>, Cardi {

	// define the card class using test driven development

	public static final int MAX_FACE = 13;
	public static final int MIN_FACE = 1;
	public static final int MAX_SUIT = 4;
	public static final int MIN_SUIT = 1;
	private static final String CLUBS = "Clubs";
	private static final String DIAMONDS = "Diamonds";
	private static final String HEARTS = "Hearts";
	private static final String SPADES = "Spades";


	
	private static final String[] SUIT_NAMES = { "", CLUBS, DIAMONDS, HEARTS, SPADES};

	private static final String[] FACE_NAMES = { "", "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack",
			"Queen", "King" };

	protected int suit;
	protected int face;

	public Card(int suitVal, int faceVal) {
		if (suitVal < MIN_SUIT || suitVal > MAX_SUIT) {
			throw new IllegalArgumentException("suit must be between" + MIN_SUIT + " and " + MAX_SUIT);
		}
		if (faceVal < MIN_FACE || faceVal > MAX_FACE) {
			throw new IllegalArgumentException("face must be between" + MIN_FACE + " and " + MAX_FACE);
		}

		this.suit = suitVal;
		this.face = faceVal;

	}

	@Override
	public int getFaceNum() {
		return face;

	}

	@Override
	public int getSuitNum() {
		return suit;
	}

	@Override
	public int getRank() {
		if (face == 1) {
			return 12 * 4 + suit;
		}
		return (face - 2) * 4 + suit;
	}

	@Override
	public String getFaceString() {
		return FACE_NAMES[face];
	}

	@Override
	public String getSuitString() {
		return SUIT_NAMES[suit];
	}

	@Override
	public String toString() {
		return getFaceString() + " of " + getSuitString();
	}

	@Override
	public boolean equals(Card c) {
//	assert c != null; //java assert method 
		if (c == null)
			return false;
		return this.face == c.face;
	}

	@Override
	public int compareTo(Card otherCard) {
		return Integer.compare(this.getRank(), otherCard.getRank());
	}

	@Override
	public int compareTo(Cardi otherCard) {
		// TODO Auto-generated method stub
		return 0;
	}

}
