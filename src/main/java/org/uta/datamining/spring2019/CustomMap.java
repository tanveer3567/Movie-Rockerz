package org.uta.datamining.spring2019;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

public class CustomMap {

	private Integer id;
	private Double score;
	private String name;
	private String url;
	private List<String> tagList;
	private TreeSet<String> matchWords;
	private LinkedHashMap<String, Double> wordsTfIdfMatch;
	private List<String> queryList;
	
	public CustomMap() {
	}
	
	public CustomMap(Integer id, Double score, String name, String url, List<String> tagList, List<String> queryList) {
		this.id = id;
		this.score = score;
		this.name = name;
		this.url = url;
		this.tagList = tagList;
		this.queryList = queryList;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getTagList() {
		return tagList;
	}

	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}

	public TreeSet<String> getMatchWords() {
		return matchWords;
	}

	public void setMatchWords(TreeSet<String> matchWords) {
		this.matchWords = matchWords;
	}

	public LinkedHashMap<String, Double> getWordsTfIdfMatch() {
		return wordsTfIdfMatch;
	}

	public void setWordsTfIdfMatch(LinkedHashMap<String, Double> wordsTfIdfMatch) {
		this.wordsTfIdfMatch = wordsTfIdfMatch;
	}

	public List<String> getQueryList() {
		return queryList;
	}

	public void setQueryList(List<String> queryList) {
		this.queryList = queryList;
	}
	
}
