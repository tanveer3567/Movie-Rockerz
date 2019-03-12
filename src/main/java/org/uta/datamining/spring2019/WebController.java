package org.uta.datamining.spring2019;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController {

	List<Result> result = null;
	LinkedHashMap<CustomMap, String> fullResult = null;
	int noOfPages = 0;

	@GetMapping("/movierockerz")
	public ModelAndView getNothing(@RequestParam(name = "page", required = true) int pageNumber,
			@RequestParam(name = "search", required = true) String search) throws FileNotFoundException {
		List<Result> partialResult = new ArrayList<Result>();
		if (pageNumber == 1) {
			fullResult = SearchAlgorithm.search(search);
			result = new ArrayList<Result>();
			fullResult.forEach((key, value) -> {
				result.add(new Result(key, value));
			});
			noOfPages = (fullResult.size() + 10) / 10;
		}
		for (int i = (pageNumber - 1) * 10; i < pageNumber * 10 && i < result.size(); i++) {
			partialResult.add(result.get(i));
		}
		ModelAndView movies = new ModelAndView("result");
		movies.addObject("count", result.size());
		movies.addObject("search", search);
		movies.addObject("movieMap", partialResult);
		movies.addObject("noOfPages", noOfPages);
		return movies;
	}
}
