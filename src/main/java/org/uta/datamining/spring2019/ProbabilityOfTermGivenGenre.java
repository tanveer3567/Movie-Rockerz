package org.uta.datamining.spring2019;

public class ProbabilityOfTermGivenGenre {

	private String term;
	private String genre;
	
	public ProbabilityOfTermGivenGenre(String term, String genre) {
		this.term = term;
		this.genre = genre;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	
}
