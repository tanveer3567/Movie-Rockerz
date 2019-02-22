package org.uta.datamining.spring2019;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController {

	@GetMapping("/nothing")
	public ModelAndView getNothing(@RequestParam(name = "search", required = true) String search)
			throws FileNotFoundException {
//		LinkedHashMap<CustomMap, String> result = SearchAlgorithm.search(search);
		ModelAndView movies = new ModelAndView("result");
//		movies.addObject("count", result.size());
//		movies.addObject("movieMap",result);
		return movies;
	}
}
