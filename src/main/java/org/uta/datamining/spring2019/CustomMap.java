package org.uta.datamining.spring2019;

public class CustomMap {

	private Integer id;
	private Double score;
	private String name;
	
	public CustomMap() {
	}

	public CustomMap(Integer id, Double score, String name) {
		this.id = id;
		this.score = score;
		this.name = name;
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
	
}
