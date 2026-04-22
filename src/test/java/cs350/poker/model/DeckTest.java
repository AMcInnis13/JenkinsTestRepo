package cs350.poker.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Comprehensive JUnit 4 tests for the {@link Deck} class.
 *
 * <p>Tests cover construction, shuffling, dealing, returning cards,
 * emptiness, card count, edge cases, and failure scenarios.
 * Uses Mockito to mock the Random class for deterministic shuffle testing.</p>
 *
 * @author Poker Demo
 * @version 1.0
 */
public class DeckTest {

    private Deck deck;

    /**
     * Sets up a fresh deck before each test.
     */
    @Before
    public void setUp() {
        deck = new Deck();
    }

    // ==================== Construction Tests ====================

    @Test
    public void testNewDeckHas52Cards() {
        assertThat(deck.numCardsLeft(), is(52));
    }

    @Test
    public void testNewDeckIsNotEmpty() {
        assertThat(deck.isEmpty(), is(false));
    }

    @Test
    public void testNewDeckContainsAllUniqueCards() {
    	Set<String> seen = new HashSet<>();
    	for(int i = 0; i < 52; i++) {
    		String cardString = deck.dealCard().toString();
    		assertThat("Duplicate:" + cardString, seen.contains(cardString), is(false));
    		seen.add(cardString);
    	}
    	assertThat(seen.size(), is(52));
    }

    @Test
    public void testNewDeckContainsCorrectSuits() {
    	
    	int[] suitCounts = new int[5]; 
        for (int i = 0; i < 52; i++) {
            Cardi c = deck.dealCard();
            suitCounts[c.getSuitNum()]++;
        }
     
        assertThat(suitCounts[1], is(13)); 
        assertThat(suitCounts[2], is(13)); 
        assertThat(suitCounts[3], is(13));
        assertThat(suitCounts[4], is(13));
    }
    

    // ==================== Shuffle Tests ====================

    @Test
    public void testShuffleResetsDeckToFull() {
        deck.dealCard(); // deal one
        deck.dealCard(); // deal another
        assertThat(deck.numCardsLeft(), is(50));

        deck.shuffle(); // shuffle resets
        assertThat(deck.numCardsLeft(), is(52));
    }

    @Test
    public void testShuffleChangesCardOrder() {
        // Deal first 5 cards before shuffle
        String[] before = new String[5];
        for (int i = 0; i < 5; i++) {
            before[i] = deck.dealCard().toString();
        }

        deck.shuffle(); // re-shuffle

        // Deal first 5 cards after shuffle
        String[] after = new String[5];
        for (int i = 0; i < 5; i++) {
            after[i] = deck.dealCard().toString();
        }

        // It's extremely unlikely all 5 are in the same order after a real shuffle
        boolean allSame = true;
        for (int i = 0; i < 5; i++) {
            if (!before[i].equals(after[i])) {
                allSame = false;
                break;
            }
        }
        assertThat("Shuffle should change card order", allSame, is(false));
    }

    @Test
    public void testShuffleWithSeededRandom() {
        // Two decks with same seed should produce same shuffle order
        Deck deck1 = new Deck(new Random(42));
        Deck deck2 = new Deck(new Random(42));

        deck1.shuffle();
        deck2.shuffle();

        for (int i = 0; i < 52; i++) {
            Cardi c1 = deck1.dealCard();
            Cardi c2 = deck2.dealCard();
            assertThat(c1.toString(), is(c2.toString()));
        }
    }



    @Test
    public void testShufflePreservesAllCards() {
        deck.shuffle();

        Set<String> cards = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            cards.add(deck.dealCard().toString());
        }
        assertThat("Shuffle must preserve all 52 unique cards", cards.size(), is(52));
    }

    @Test
    public void testMultipleShuffles() {
        deck.shuffle();
        deck.shuffle();
        deck.shuffle(); // shuffle three times
        assertThat(deck.numCardsLeft(), is(52)); // still 52 cards
    }
    
    @Test
    public void testShuffleMock() {
    	
    	Random mockRandom = mock(Random.class);
    	when(mockRandom.nextInt(anyInt())).thenReturn(0);
    	
    	Deck mockDeck = new Deck(mockRandom);
    	mockDeck.shuffle();
    	
    	verify(mockRandom, times(51)).nextInt(anyInt());
    }

    // ==================== Deal Tests ====================

    @Test
    public void testDealCardReducesCount() {
        deck.dealCard();
        assertThat(deck.numCardsLeft(), is(51));
    }

    @Test
    public void testDealCardReturnsNonNull() {
        Card dealt = deck.dealCard();
        assertThat(dealt, is(notNullValue()));
    }

    @Test
    public void testDealAllCards() {
        for (int i = 0; i < 52; i++) {
            Card c = deck.dealCard();
            assertThat(c, is(notNullValue()));
        }
        assertThat(deck.isEmpty(), is(true));
        assertThat(deck.numCardsLeft(), is(0));
    }

    @Test
    public void testDealCardReturnsValidCard() {
        Cardi c = deck.dealCard();
        assertThat(c.getSuitNum(), greaterThanOrEqualTo(Card.MIN_SUIT));
        assertThat(c.getSuitNum(), lessThanOrEqualTo(Card.MAX_SUIT));
        assertThat(c.getFaceNum(), greaterThanOrEqualTo(Card.MIN_FACE));
        assertThat(c.getFaceNum(), lessThanOrEqualTo(Card.MAX_FACE));
    }

    @Test(expected = IllegalStateException.class)
    public void testDealFromEmptyDeck() {
        for (int i = 0; i < 52; i++) {
            deck.dealCard(); // exhaust the deck
        }
        deck.dealCard(); // should throw
    }

    // ==================== Return Card Tests ====================

    @Test
    public void testReturnCardIncreasesCount() {
        Card c = deck.dealCard(); // 51 left
        deck.returnCard(c);      // back to 52
        assertThat(deck.numCardsLeft(), is(52));
    }

    @Test
    public void testReturnCardMakesItDealable() {
        Card dealt = deck.dealCard();
        deck.returnCard(dealt);
        Card reDealt = deck.dealCard(); // should get the returned card
        assertThat(reDealt, is(notNullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReturnNullCard() {
        deck.dealCard(); // make room
        deck.returnCard(null); // should throw
    }

    @Test(expected = IllegalStateException.class)
    public void testReturnCardToFullDeck() {
        Card extra = new Card(1, 1); // a card not from this deck
        deck.returnCard(extra); // deck is already full — should throw
    }

    @Test
    public void testReturnMultipleCards() {
        Card c1 = deck.dealCard();
        Card c2 = deck.dealCard();
        Card c3 = deck.dealCard();
        assertThat(deck.numCardsLeft(), is(49));

        deck.returnCard(c3);
        deck.returnCard(c2);
        deck.returnCard(c1);
        assertThat(deck.numCardsLeft(), is(52));
    }

    // ==================== isEmpty / numCardsLeft Tests ====================

    @Test
    public void testIsEmptyOnNewDeck() {
        assertThat(deck.isEmpty(), is(false));
    }

    @Test
    public void testIsEmptyAfterDealingAll() {
        for (int i = 0; i < 52; i++) {
            deck.dealCard();
        }
        assertThat(deck.isEmpty(), is(true));
    }

    @Test
    public void testNumCardsLeftDecrements() {
        for (int i = 52; i > 0; i--) {
            assertThat(deck.numCardsLeft(), is(i));
            deck.dealCard();
        }
        assertThat(deck.numCardsLeft(), is(0));
    }

    @Test
    public void testIsEmptyAfterReturnNotEmpty() {
        for (int i = 0; i < 52; i++) {
            deck.dealCard();
        }
        assertThat(deck.isEmpty(), is(true));

        deck.returnCard(new Card(1, 1));
        assertThat(deck.isEmpty(), is(false));
    }
}
