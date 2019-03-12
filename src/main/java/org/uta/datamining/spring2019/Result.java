package org.uta.datamining.spring2019;

public class Result {

	private CustomMap customMap;
	private String overview;

	public Result(CustomMap customMap, String overview) {
		this.customMap = customMap;
		this.overview = overview;
	}

	public CustomMap getCustomMap() {
		return customMap;
	}

	public void setCustomMap(CustomMap customMap) {
		this.customMap = customMap;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

}
