package cs350.poker.model;

public interface Cardi {

	int getFaceNum();

	int getSuitNum();

	int getRank();

	String getFaceString();

	String getSuitString();

	String toString();

	boolean equals(Card c);

	int compareTo(Cardi otherCard);

}