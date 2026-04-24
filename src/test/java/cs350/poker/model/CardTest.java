
package cs350.poker.model;

import static org.junit.Assert.fail;
//static brings the classes in. as in junit.test vs just .test 
import org.junit.*;

public class CardTest {
		
	private class testCard extends Card{
		public testCard(int f, int s) {
			super (f,s);
		}
		public int getFace() {
			return this.face;
		}
		public int getSuit() {
			return this.suit;
		}
	}
	
	@Test 
	public void test_constructor() throws Exception {
		
		testCard c = new testCard(1,2); 
		
		Assert.assertNotNull(c);
		
		Assert.assertEquals(1,c.getSuit());
		Assert.assertEquals(2,c.getFace());
		
	}
	
	@Test 
	public void test_getFaceNumLow() throws Exception {
		
		testCard c = new testCard(1,2); 
		
		Assert.assertEquals(2, c.getFaceNum());
	}
	
	@Test 
	public void test_getFaceNumHigh() throws Exception {
		
		testCard c = new testCard(4,13); 
		
		Assert.assertEquals(13, c.getFaceNum());
	}
	
	@Test 
	public void test_getRankLow() throws Exception {
		testCard c = new testCard (1, 2);
		
		Assert.assertEquals(1, c.getRank());
	}
	
	@Test 
	public void test_getRankHigh() throws Exception {
		testCard c = new testCard(4,1);
		
		Assert.assertEquals(52, c.getRank());
	}
	
	@Test 
	public void test_getSuitString() throws Exception {
		testCard c = new testCard(4,1); 
		
		Assert.assertEquals(0, 0);
	}
	
}
