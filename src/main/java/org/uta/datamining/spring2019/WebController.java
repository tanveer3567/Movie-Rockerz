package org.uta.datamining.spring2019;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController {

	List<Result> result = null;
	LinkedHashMap<CustomMap, String> fullResult = null;
	int noOfPages = 0;
	FullClassiferResult cResult = null;

	@GetMapping("/movierockerz")
	public ModelAndView getNothing(@RequestParam(name = "page", required = false) Integer pageNumber,
			@RequestParam(name = "search", required = false) String search) throws FileNotFoundException {

		if (StringUtils.isBlank(search)) {
			return new ModelAndView("index");
		}
		List<Result> partialResult = new ArrayList<Result>();
		if (pageNumber == 1) {
			fullResult = SearchAlgorithm.search(search);
			result = new ArrayList<Result>();
			fullResult.forEach((key, value) -> {
				result.add(new Result(key, value));
			});
			noOfPages = (fullResult.size() + 25) / 25;
		}
		for (int i = (pageNumber - 1) * 25; i < pageNumber * 25 && i < result.size(); i++) {
			partialResult.add(result.get(i));
		}
		ModelAndView movies = new ModelAndView("result");
		movies.addObject("count", result.size());
		movies.addObject("search", search);
		movies.addObject("movieMap", partialResult);
		movies.addObject("noOfPages", noOfPages);
		return movies;
	}

	@GetMapping("/movierockerz/classifier")
	public ModelAndView getNothing(@RequestParam(name = "classify", required = false) String query)
			throws FileNotFoundException {

		if (StringUtils.isBlank(query)) {
			return new ModelAndView("classify");
		}
		cResult = ClassiferAlgorithm.classify(query);
		cResult.getReverseSortedMap().forEach((key, value) -> {
			System.out.println(key + ": " + value);
		});
		ModelAndView genre = new ModelAndView("classifyResult");
		genre.addObject("result", cResult.getReverseSortedMap());
		genre.addObject("query", query);
		int i = 1;
		for (Entry<String, Double> entry : cResult.getReverseSortedMap().entrySet()) {
			if (i < 5)
				genre.addObject("genre" + i, entry.getKey());
			else
				break;
			i++;
		}
		return genre;
	}

	@GetMapping("/movierockerz/priorProbabilities")
	public ModelAndView claculationStep1() throws FileNotFoundException {
		ModelAndView genre = new ModelAndView("priorProbabilities");
		genre.addObject("result", cResult.getProbabilityOfGenre());
		return genre;
	}

	@GetMapping("/movierockerz/probabilityOfTermGivenGenre")
	public ModelAndView claculationStep2() throws FileNotFoundException {
		ModelAndView genre = new ModelAndView("probabilityOfTermGivenGenre");
		genre.addObject("result", cResult.getScore());
		return genre;
	}
	
	@GetMapping("/movierockerz/evaluation")
	public ModelAndView evaluation() throws FileNotFoundException {
		ModelAndView genre = new ModelAndView("evaluation");
		return genre;
	}
}
