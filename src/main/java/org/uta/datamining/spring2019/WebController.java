package org.uta.datamining.spring2019;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
			@RequestParam(name = "search", required = false) String search, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws FileNotFoundException {

		if (StringUtils.isBlank(search)) {
			Cookie[] cookies = httpRequest.getCookies();
			if (Objects.nonNull(cookies) && cookies.length > 0) {
				String movieId = "";
				for (Cookie cookie : cookies) {
					System.out.println(cookie.getName());
					if (cookie.getName().equalsIgnoreCase("recommendations")) {
						movieId = cookie.getValue();
					}
				}
				if(!StringUtils.isBlank(movieId)) {
					MovieDetails movieDetails = MovieDetailsService.getMovieDetails(Integer.parseInt(movieId));
					LinkedHashMap<CustomMap, String> searchTemp = SearchAlgorithm.search(movieDetails.getOverview());
					LinkedHashMap<CustomMap, String> searchResult = new LinkedHashMap<CustomMap, String>();
					int i = 0;
					for (Entry<CustomMap, String> entry : searchTemp.entrySet()) {
						if (i < 21 && i > 0) {
							searchResult.put(entry.getKey(), entry.getValue());
						}
						i++;
					}
					ModelAndView model = new ModelAndView("index");
					model.addObject("result", movieDetails);
					model.addObject("searchResult", searchResult);
					return model;
				}else {
					ModelAndView model = new ModelAndView("index");
					return model;
				}	
			}else {
				ModelAndView model = new ModelAndView("index");
				return model;
			}
		} else {
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
			partialResult.forEach(result -> {
				LinkedHashMap<String, Double> wordsTfIdfMatch = result.getCustomMap().getWordsTfIdfMatch();
//				wordsTfIdfMatch.forEach((key, value) -> System.out.println(key + " : " + value));
			});
			ModelAndView movies = new ModelAndView("result");
			movies.addObject("count", result.size());
			movies.addObject("search", search);
			movies.addObject("movieMap", partialResult);
			movies.addObject("noOfPages", noOfPages);
			return movies;
		}
	}

	@GetMapping("/movierockerz/classifier")
	public ModelAndView getNothing(@RequestParam(name = "classify", required = false) String query)
			throws FileNotFoundException {

		if (StringUtils.isBlank(query)) {
			return new ModelAndView("classify");
		}
		cResult = ClassiferAlgorithm.classify(query);
//		cResult.getReverseSortedMap().forEach((key, value) -> {
//			System.out.println(key + ": " + value);
//		});
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

	@GetMapping("/movierockerz/movieDetails")
	public ModelAndView getMovieDetails(@RequestParam(name = "movieId", required = true) String movieId,
			HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws FileNotFoundException {

		Cookie cookie = new Cookie("recommendations", movieId);
		httpResponse.addCookie(cookie);
		MovieDetails movieDetails = MovieDetailsService.getMovieDetails(Integer.parseInt(movieId));
		LinkedHashMap<CustomMap, String> search = SearchAlgorithm.search(movieDetails.getOverview());
		LinkedHashMap<CustomMap, String> searchResult = new LinkedHashMap<CustomMap, String>();
		int i = 0;
		for (Entry<CustomMap, String> entry : search.entrySet()) {
			if (i < 21 && i > 0) {
				searchResult.put(entry.getKey(), entry.getValue());
			}
			i++;
		}
		ModelAndView model = new ModelAndView("recommender");
		model.addObject("result", movieDetails);
		model.addObject("searchResult", searchResult);
		return model;
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

	@GetMapping("/movierockerz/fullResults")
	public ModelAndView geTfIdftResults() throws IOException {
		ModelAndView model = new ModelAndView("fullResults");
		String property = System.getProperty("user.dir");
		FileReader reader = new FileReader(new File(property + "/output.txt"));
		BufferedReader buffReader = new BufferedReader(reader);
		ArrayList<String> tfList = new ArrayList<String>();
		while (true) {
			String line = buffReader.readLine();
			if (Objects.nonNull(line))
				tfList.add(line);
			else
				break;
		}
		buffReader.close();
		model.addObject("result", tfList);
		return model;
	}

	@GetMapping("/movierockerz/vectors")
	public ModelAndView getFullSearchResults() throws IOException {
		ModelAndView model = new ModelAndView("vectors");
		String property = System.getProperty("user.dir");
		FileReader reader = new FileReader(new File(property + "/final.txt"));
		BufferedReader buffReader = new BufferedReader(reader);
		ArrayList<String> tfList = new ArrayList<String>();
		while (true) {
			String line = buffReader.readLine();
			if (Objects.nonNull(line))
				tfList.add(line);
			else
				break;
		}
		buffReader.close();
		model.addObject("result", tfList);
		return model;
	}

	@GetMapping("/movierockerz/tf")
	public ModelAndView getTfResults() throws IOException {
		ModelAndView model = new ModelAndView("tf");
		String property = System.getProperty("user.dir");
		FileReader reader = new FileReader(new File(property + "/Tf.txt"));
		BufferedReader buffReader = new BufferedReader(reader);
		ArrayList<String> tfList = new ArrayList<String>();
		while (true) {
			String line = buffReader.readLine();
			if (Objects.nonNull(line))
				tfList.add(line);
			else
				break;
		}
		buffReader.close();
		model.addObject("result", tfList);
		return model;
	}

	@GetMapping("/movierockerz/idf")
	public ModelAndView getIdfResults() throws IOException {
		ModelAndView model = new ModelAndView("idf");
		String property = System.getProperty("user.dir");
		FileReader reader = new FileReader(new File(property + "/idf.txt"));
		BufferedReader buffReader = new BufferedReader(reader);
		ArrayList<String> tfList = new ArrayList<String>();
		while (true) {
			String line = buffReader.readLine();
			if (Objects.nonNull(line))
				tfList.add(line);
			else
				break;
		}
		buffReader.close();
		model.addObject("result", tfList);
		return model;
	}
}
