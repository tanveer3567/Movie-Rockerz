package org.uta.datamining.spring2019;

import java.util.LinkedHashMap;

public class FullClassiferResult {

	private LinkedHashMap<String, Double> reverseSortedMap = null;
	private LinkedHashMap<String, Double> probabilityOfGenre = null;
	private LinkedHashMap<ProbabilityOfTermGivenGenre, Double> score = null;
	
	public FullClassiferResult(LinkedHashMap<String, Double> reverseSortedMap,
			LinkedHashMap<String, Double> probabilityOfGenre,
			LinkedHashMap<ProbabilityOfTermGivenGenre, Double> score) {
		this.reverseSortedMap = reverseSortedMap;
		this.probabilityOfGenre = probabilityOfGenre;
		this.score = score;
	}
	
	public LinkedHashMap<String, Double> getReverseSortedMap() {
		return reverseSortedMap;
	}
	public void setReverseSortedMap(LinkedHashMap<String, Double> reverseSortedMap) {
		this.reverseSortedMap = reverseSortedMap;
	}
	public LinkedHashMap<String, Double> getProbabilityOfGenre() {
		return probabilityOfGenre;
	}
	public void setProbabilityOfGenre(LinkedHashMap<String, Double> probabilityOfGenre) {
		this.probabilityOfGenre = probabilityOfGenre;
	}
	public LinkedHashMap<ProbabilityOfTermGivenGenre, Double> getScore() {
		return score;
	}
	public void setScore(LinkedHashMap<ProbabilityOfTermGivenGenre, Double> score) {
		this.score = score;
	}
	
	
}
