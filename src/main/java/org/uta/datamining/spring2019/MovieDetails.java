package org.uta.datamining.spring2019;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class MovieDetails {

	private int movieId;
	private BigInteger budget;
	private BigInteger revenue;
	private String url;
	private List<String> tags;
	private List<String> genres;
	private String title;
	private String overview;
	private int runtime;
	private Date releaseDate;
	private double popularity;
	private int noOfVotes;
	private double avgVotes;
	private List<String> queryList;
	
	public MovieDetails(int movieId, BigInteger budget, BigInteger revenue, String url, List<String> tags,
			List<String> genres, String title, String overview, int runtime, Date releaseDate, double popularity,
			int noOfVotes, double avgVotes, List<String> queryList) {
		super();
		this.movieId = movieId;
		this.budget = budget;
		this.revenue = revenue;
		this.url = url;
		this.tags = tags;
		this.genres = genres;
		this.title = title;
		this.overview = overview;
		this.runtime = runtime;
		this.releaseDate = releaseDate;
		this.popularity = popularity;
		this.noOfVotes = noOfVotes;
		this.avgVotes = avgVotes;
		this.queryList = queryList;
	}
	
	public MovieDetails(int movieId, BigInteger budget, BigInteger revenue, String url, List<String> tags,
			List<String> genres, String title, String overview, int runtime, Date releaseDate, double popularity,
			int noOfVotes, double avgVotes) {
		super();
		this.movieId = movieId;
		this.budget = budget;
		this.revenue = revenue;
		this.url = url;
		this.tags = tags;
		this.genres = genres;
		this.title = title;
		this.overview = overview;
		this.runtime = runtime;
		this.releaseDate = releaseDate;
		this.popularity = popularity;
		this.noOfVotes = noOfVotes;
		this.avgVotes = avgVotes;
	}

	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public BigInteger getBudget() {
		return budget;
	}
	public void setBudget(BigInteger budget) {
		this.budget = budget;
	}
	public BigInteger getRevenue() {
		return revenue;
	}
	public void setRevenue(BigInteger revenue) {
		this.revenue = revenue;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public List<String> getGenres() {
		return genres;
	}
	public void setGenres(List<String> genres) {
		this.genres = genres;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public int getRuntime() {
		return runtime;
	}
	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public double getPopularity() {
		return popularity;
	}
	public void setPopularity(double popularity) {
		this.popularity = popularity;
	}
	public int getNoOfVotes() {
		return noOfVotes;
	}
	public void setNoOfVotes(int noOfVotes) {
		this.noOfVotes = noOfVotes;
	}
	public double getAvgVotes() {
		return avgVotes;
	}
	public void setAvgVotes(double avgVotes) {
		this.avgVotes = avgVotes;
	}
	public List<String> getQueryList() {
		return queryList;
	}
	public void setQueryList(List<String> queryList) {
		this.queryList = queryList;
	}
	
	
	
}
