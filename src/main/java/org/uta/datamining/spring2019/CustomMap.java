package org.uta.datamining.spring2019;

import java.util.List;

public class CustomMap {

	private Integer id;
	private Double score;
	private String name;
	private String url;
	private List<String> tagList;
	
	public CustomMap() {
	}
	
	public CustomMap(Integer id, Double score, String name, String url, List<String> tagList) {
		this.id = id;
		this.score = score;
		this.name = name;
		this.url = url;
		this.tagList = tagList;
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
	
}
