package org.uta.datamining.spring2019;

import java.util.LinkedHashMap;

public class Statistic {

	private int genreCount;
	private int movieId;
	private LinkedHashMap<String, Double> genreScore;
	private double precision;
	private double recall;
	private double fMeasure;
	
	public Statistic(int genreCount, int movieId, LinkedHashMap<String, Double> genreScore, double precision,
			double recall, double fMeasure) {
		this.genreCount = genreCount;
		this.movieId = movieId;
		this.genreScore = genreScore;
		this.precision = precision;
		this.recall = recall;
		this.fMeasure = fMeasure;
	}
	
	public int getGenreCount() {
		return genreCount;
	}
	public void setGenreCount(int genreCount) {
		this.genreCount = genreCount;
	}
	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public LinkedHashMap<String, Double> getGenreScore() {
		return genreScore;
	}
	public void setGenreScore(LinkedHashMap<String, Double> genreScore) {
		this.genreScore = genreScore;
	}
	public double getPrecision() {
		return precision;
	}
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	public double getRecall() {
		return recall;
	}
	public void setRecall(double recall) {
		this.recall = recall;
	}
	public double getfMeasure() {
		return fMeasure;
	}
	public void setfMeasure(double fMeasure) {
		this.fMeasure = fMeasure;
	}
	
	
}
