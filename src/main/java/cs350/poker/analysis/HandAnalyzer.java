package cs350.poker.analysis;

import java.util.List;

import cs350.poker.model.Card;
import cs350.poker.model.Cardi;
import cs350.poker.model.Hand;

/**
 * Analyzes a five-card poker hand to determine its type (e.g., pair, flush,
 * straight, full house, etc.).
 *
 * <p>
 * Upon construction, the analyzer accepts a reference to a {@link Hand} and
 * evaluates it to determine the poker hand type. The analyzer provides boolean
 * query methods for each hand type as well as a descriptive string identifying
 * the best hand type.
 * </p>
 *
 * <p>
 * Hand types in ascending order of value:
 * </p>
 * <ol>
 * <li>High Card</li>
 * <li>One Pair</li>
 * <li>Two Pair</li>
 * <li>Three of a Kind</li>
 * <li>Straight</li>
 * <li>Flush</li>
 * <li>Full House</li>
 * <li>Four of a Kind</li>
 * <li>Straight Flush</li>
 * <li>Royal Flush</li>
 * </ol>
 *
 * @author Poker Demo
 * @version 1.0
 */
public class HandAnalyzer {

	/** Numeric type constants for hand ranking comparisons. */
	public static final int HIGH_CARD = 0;
	public static final int ONE_PAIR = 1;
	public static final int TWO_PAIR = 2;
	public static final int THREE_OF_A_KIND = 3;
	public static final int STRAIGHT = 4;
	public static final int FLUSH = 5;
	public static final int FULL_HOUSE = 6;
	public static final int FOUR_OF_A_KIND = 7;
	public static final int STRAIGHT_FLUSH = 8;
	public static final int ROYAL_FLUSH = 9;

	/** Human-readable names for each hand type, indexed by type constant. */
	private static final String[] HAND_TYPE_NAMES = { "High Card", "One Pair", "Two Pair", "Three of a Kind",
			"Straight", "Flush", "Full House", "Four of a Kind", "Straight Flush", "Royal Flush" };

	protected final Hand hand; // reference to the hand being analyzed
	private final int handType; // computed hand type constant

	// cached analysis results
	private final int[] faceCounts; // count of each face value (index 0..13; 0 unused)
	private final boolean flushFlag;
	private final boolean straightFlag;

	/**
	 * Constructs a HandAnalyzer for the specified hand.
	 *
	 * <p>
	 * The hand is analyzed immediately upon construction. The hand should contain
	 * exactly 5 cards for standard poker analysis.
	 * </p>
	 *
	 * @param hand the Hand to analyze
	 * @throws IllegalArgumentException if hand is null or does not contain exactly
	 *                                  5 cards
	 */
	public HandAnalyzer(Hand hand) {
		if (hand == null) { // null guard
			throw new IllegalArgumentException("Hand cannot be null.");
		}
		if (hand.size() != 5) { // must be a five-card hand
			throw new IllegalArgumentException("Hand must contain exactly 5 cards, but has " + hand.size());
		}
		this.hand = hand;

		/* Phase 1: Count occurrences of each face value */
		faceCounts = countFaces();

		/* Phase 2: Detect flush and straight */
		flushFlag = detectFlush();
		straightFlag = detectStraight();

		/* Phase 3: Determine the best hand type */
		handType = evaluateHandType();
	}

	/**
	 * Counts the occurrences of each face value in the hand.
	 *
	 * @return an array where index i holds the count of cards with face value i
	 */
	protected int[] countFaces() {
		int[] counts = new int[Card.MAX_FACE + 1]; // indices 0..13
		List<Cardi> cards = hand.getCards();
		for (Cardi c : cards) {
			counts[c.getFaceNum()]++; // tally each face value
		}
		return counts;
	}

	/**
	 * Detects whether all five cards share the same suit (a flush).
	 *
	 * @return {@code true} if all cards have the same suit
	 */
	private boolean detectFlush() {
		List<Cardi> cards = hand.getCards();
		int firstSuit = cards.get(0).getSuitNum();
		for (Cardi c : cards) {
			if (c.getSuitNum() != firstSuit)
				return false;
		}
		return true; // all suits match
	}

	/**
	 * Detects whether the hand forms a straight (five consecutive face values).
	 *
	 * <p>
	 * Handles the special case of the Ace-low straight (A-2-3-4-5), also known as
	 * the "wheel", as well as the standard straight detection.
	 * </p>
	 *
	 * @return {@code true} if the hand is a straight
	 */
	private boolean detectStraight() {
		/*
		 * Build a sorted list of face values to check for consecutive sequence. We sort
		 * a copy of the hand by rank and examine the result.
		 */
		Hand tempHand = new Hand();
		for (Cardi c : hand.getCards()) {
			tempHand.addCard(c);
		}
		tempHand.sortByRank(); // sort by rank (Ace high)
		List<Cardi> sorted = tempHand.getCards();

		/* Check for Ace-low straight (wheel): A-2-3-4-5 */
		if (isWheel(sorted)) {
			return true;
		}
		
		if (isAceHighStraight(sorted)) {
			return true;
		}

		/* Standard consecutive check using rank differences */
		for (int i = 1; i < sorted.size(); i++) {
			int faceDiff = sorted.get(i).getFaceNum() - sorted.get(i - 1).getFaceNum();
			/*
			 * In a straight, consecutive cards differ by exactly 4 in rank because each
			 * face value spans 4 suits (e.g., 3♣=5, 3♦=6, 3♥=7, 3♠=8; 4♣=9). The gap
			 * between face values in rank is always 4.
			 */
			if (faceDiff != 1) { // not consecutive face values
				return false;
			}
		}
		return true;
	}

	private boolean isAceHighStraight(List<Cardi> sorted) {
		int[] faceVals = sorted.stream().mapToInt(Cardi::getFaceNum).sorted().toArray();
		
		return faceVals[0] == 1 && faceVals[1] == 10 && faceVals[2] == 11 
		        && faceVals[3] == 12 && faceVals[4] == 13;
	}

	/**
	 * Checks if the sorted cards form an Ace-low straight (A-2-3-4-5).
	 *
	 * @param sorted cards sorted by rank (ascending)
	 * @return {@code true} if the hand is a wheel (A-2-3-4-5)
	 */
	private boolean isWheel(List<Cardi> sorted) {
		// In a rank-sorted hand, A-2-3-4-5 would appear as 2,3,4,5,A
		// because Ace has the highest rank. Check face values directly.
		int[] faceVals = sorted.stream().mapToInt(Cardi::getFaceNum).sorted().toArray();
		return faceVals[0] == 1 && faceVals[1] == 2 && faceVals[2] == 3 && faceVals[3] == 4 && faceVals[4] == 5; // A,2,3,4,5
	}

	/**
	 * Evaluates and determines the best poker hand type.
	 *
	 * <p>
	 * Checks from highest to lowest hand type to find the best match.
	 * </p>
	 *
	 * @return the hand type constant (e.g., {@link #ROYAL_FLUSH})
	 */
	protected int evaluateHandType() {
		/* Count how many pairs, trips, and quads are present */
		int pairs = 0;
		int threeOfAKind = 0;
		int fourOfAKind = 0;

		for (int i = Card.MIN_FACE; i <= Card.MAX_FACE; i++) {
			if (faceCounts[i] == 2)
				pairs++;
			else if (faceCounts[i] == 3)
				threeOfAKind++;
			else if (faceCounts[i] == 4)
				fourOfAKind++;
		}

		/* Evaluate from best to worst hand type */
		if (flushFlag && straightFlag) {
			// Check if it's a royal flush (10-J-Q-K-A all same suit)
			if (faceCounts[1] == 1 && faceCounts[10] == 1 && faceCounts[11] == 1 && faceCounts[12] == 1
					&& faceCounts[13] == 1) {
				return ROYAL_FLUSH; // the holy grail
			}
			return STRAIGHT_FLUSH; // flush + straight but not royal
		}
		if (fourOfAKind > 0)
			return FOUR_OF_A_KIND;
		
		if (threeOfAKind > 0 && pairs ==1)
			return FULL_HOUSE;

		if (flushFlag)
			return FLUSH;
		if (straightFlag)
			return STRAIGHT;
		if (threeOfAKind > 0)
			return THREE_OF_A_KIND;
		if (pairs == 2)
			return TWO_PAIR;
		if (pairs == 1)
			return ONE_PAIR;
		return HIGH_CARD; // nothing special
	}

	/**
	 * Returns the numeric type constant for this hand.
	 *
	 * @return the hand type constant (0=High Card through 9=Royal Flush)
	 */
	public int getHandType() {
		return handType;
	}

	/**
	 * Returns the human-readable name of this hand's type.
	 *
	 * @return a string such as "Full House", "Straight", "High Card", etc.
	 */
	public String getHandTypeName() {
		return HAND_TYPE_NAMES[handType];
	}

	/** @return {@code true} if the hand is a Royal Flush */
	public boolean isRoyalFlush() {
		return handType == ROYAL_FLUSH;
	}

	/** @return {@code true} if the hand is a Straight Flush (but not Royal) */
	public boolean isStraightFlush() {
		return handType == STRAIGHT_FLUSH;
	}

	/** @return {@code true} if the hand is Four of a Kind */
	public boolean isFourOfAKind() {
		return handType == FOUR_OF_A_KIND;
	}

	/** @return {@code true} if the hand is a Full House */
	public boolean isFullHouse() {
		return handType == FULL_HOUSE;
	}

	/** @return {@code true} if the hand is a Flush (but not Straight Flush) */
	public boolean isFlush() {
		return handType == FLUSH;
	}

	/** @return {@code true} if the hand is a Straight (but not Straight Flush) */
	public boolean isStraight() {
		return handType == STRAIGHT;
	}

	/** @return {@code true} if the hand is Three of a Kind */
	public boolean isThreeOfAKind() {
		return handType == THREE_OF_A_KIND;
	}

	/** @return {@code true} if the hand is Two Pair */
	public boolean isTwoPair() {
		return handType == TWO_PAIR;
	}

	/** @return {@code true} if the hand is One Pair */
	public boolean isOnePair() {
		return handType == ONE_PAIR;
	}

	/** @return {@code true} if the hand is only a High Card */
	public boolean isHighCard() {
		return handType == HIGH_CARD;
	}

	/**
	 * Returns a reference to the hand being analyzed.
	 *
	 * @return the analyzed Hand
	 */
	public Hand getHand() {
		return hand;
	}

	/**
	 * Returns a string representation including the hand and its type.
	 *
	 * @return a descriptive string of the analysis result
	 */
	@Override
	public String toString() {
		return "Hand Type: " + getHandTypeName() + "\n" + hand.toString();
	}
}
