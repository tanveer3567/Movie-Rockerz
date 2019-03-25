package org.uta.datamining.spring2019;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import opennlp.tools.stemmer.PorterStemmer;

public class ClassiferAlgorithm {

	static String[] stopWords = { "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any",
			"are", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by",
			"could", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from", "further", "had",
			"has", "have", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him",
			"himself", "his", "how", "how's", "I", "I'd", "I'll", "I'm", "I've", "if", "in", "into", "is", "it", "it's",
			"its", "itself", "let's", "me", "more", "most", "my", "myself", "nor", "of", "on", "once", "only", "or",
			"other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "she", "she'd", "she'll",
			"she's", "should", "so", "some", "such", "than", "that", "thats", "that's", "the", "their", "theirs",
			"them", "themselves", "then", "there", "theres", "there's", "these", "they", "they'd", "they'll", "they're",
			"they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "we", "we'd",
			"we'll", "we're", "we've", "were", "what", "what's", "when", "when's", "where", "where's", "which", "while",
			"who", "who's", "whom", "why", "why's", "with", "would", "you", "you'd", "you'll", "you're", "you've",
			"your", "yours", "yourself", "yourselves" };

	static List<String> docTerms = null;
	static LinkedHashMap<Integer, ArrayList<String>> documentMap = new LinkedHashMap<Integer, ArrayList<String>>();
	static LinkedHashSet<String> hashSet = new LinkedHashSet<String>();
	static ArrayList<String> nameSet = null;
	private static LinkedHashMap<Integer, List<String>> tagsMap = new LinkedHashMap<Integer, List<String>>();
	static TreeSet<String> genreSet = new TreeSet<String>();
	static TreeMap<String, List<String>> genreMap = new TreeMap<String, List<String>>();
	static LinkedHashMap<ProbabilityOfTermGivenGenre, Double> score = new LinkedHashMap<ProbabilityOfTermGivenGenre, Double>();
	static LinkedHashMap<String, Double> probabilityOfGenre = new LinkedHashMap<String, Double>();

	public static void preload() {

		String moviesDataSet = "/src/tmdb_5000_movies.csv";
		try {
			String property = System.getProperty("user.dir");
			FileReader filereader = new FileReader(new File(property + moviesDataSet));
			CSVReader csvReader = new CSVReader(filereader);
			String[] nextRecord;
			int k = 0;
			while ((nextRecord = csvReader.readNext()) != null) {
				if (k > 0) {
					List<String> genre = readTags(nextRecord, 1);
					int record = Integer.parseInt(nextRecord[3]);
					String name = nextRecord[6];
					String[] nameSplit = name.toLowerCase().split(" ");
					readName(nameSplit);
					String overview = nextRecord[7];
					String[] split = overview.toLowerCase().split(" ");
					for (int i = 0; i < split.length; i++) {
						split[i] = stem(split[i]);
					}
					List<String> tags = readTags(nextRecord, 4);
					docTerms = Arrays.asList(split);
					ArrayList<String> tempList = new ArrayList<String>(docTerms);
					tempList.addAll(tags);
					tempList.addAll(nameSet);
					documentMap.put(record, tempList);
					tagsMap.put(record, genre);
				}
				k++;
			}

			tagsMap.forEach((id, value) -> {
				value.forEach(genre -> genreSet.add(genre));
			});

			documentMap.forEach((id, termList) -> {
				for (int i = 0; i < termList.size(); i++) {
					if (checkStopWord(termList.get(i))) {
						termList.remove(i);
						i--;
					} else {
						String removePunctuations = removePunctuations(termList.get(i));
						if (Objects.nonNull(removePunctuations))
							termList.set(i, removePunctuations);
						hashSet.add(termList.get(i));
					}
				}
			});

			documentMap.forEach((id, terms) -> {
				genreSet.forEach(genre -> {
					if (tagsMap.get(id).contains(genre)) {
						if (genreMap.containsKey(genre)) {
							genreMap.get(genre).addAll(terms);
						} else {
							genreMap.put(genre, terms);
						}
					}
				});
			});

			genreSet.forEach(genre -> {
				List<Integer> idList = new ArrayList<Integer>();
				tagsMap.forEach((id, tagList) -> {
					if (tagList.contains(genre)) {
						idList.add(id);
					}
				});
				probabilityOfGenre.put(genre, (double) idList.size() / documentMap.size());
			});

			LinkedHashMap<ProbabilityOfTermGivenGenre, Integer> termCounterMap = new LinkedHashMap<ProbabilityOfTermGivenGenre, Integer>();
			hashSet.forEach(term -> {
				for (Entry<String, List<String>> entry : genreMap.entrySet()) {
					int counter = 0;
					for (String localTerm : entry.getValue()) {
						if (term.equalsIgnoreCase(localTerm)) {
							counter++;
						}
					}
					termCounterMap.put(new ProbabilityOfTermGivenGenre(term, entry.getKey()), counter);
				}
			});

			termCounterMap.forEach((key, value) -> {
				int a = value + 1;
				int b = genreMap.get(key.getGenre()).size();
				int c = hashSet.size();
				double result = (double) a / (double) (b + c);
				score.put(new ProbabilityOfTermGivenGenre(key.getTerm(), key.getGenre()), result);
			});
			csvReader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static LinkedList<String> classify(String query) {

		LinkedHashMap<String, Double> finalScoreMap = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> reverseSortedMap = new LinkedHashMap<String, Double>();
		LinkedList<String> finalList = new LinkedList<String>();
		if (StringUtils.isNoneBlank(query)) {
			String[] split = query.split(" ");
			for (String tempTerm : split) {
				tempTerm = stem(tempTerm);
			}
			ArrayList<String> splitList = new ArrayList<String>(Arrays.asList(split));
			ArrayList<String> queryList = new ArrayList<String>();
			for (int i = 0; i < splitList.size(); i++) {
				if (checkStopWord(splitList.get(i))) {
					splitList.remove(i);
					i--;
				} else {
					String removePunctuations = removePunctuations(split[i]);
					if (Objects.nonNull(removePunctuations))
						splitList.set(i, removePunctuations);
					queryList.add(splitList.get(i));
				}
			}
			probabilityOfGenre.forEach((genre, probability) -> {
				Double finalScore = probability;
				for (String qTerm : queryList) {
					for (Entry<ProbabilityOfTermGivenGenre, Double> entry : score.entrySet()) {
						if (entry.getKey().getTerm().equalsIgnoreCase(qTerm)
								&& entry.getKey().getGenre().equalsIgnoreCase(genre)) {
							finalScore *= entry.getValue();
							break;
						}
					}
				}
				finalScoreMap.put(genre, finalScore);
			});

			finalScoreMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
					.forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

			reverseSortedMap.forEach((key, value) -> {
				finalList.add(key);
			});
		}
		return finalList;
	}

	private static List<String> readTags(String[] nextRecord, int val)
			throws JsonParseException, JsonMappingException, IOException {

		List<String> tagsList = new ArrayList<String>();
		String tagsJson = nextRecord[val];
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<Tag> tagList = mapper.readValue(tagsJson, new TypeReference<ArrayList<Tag>>() {
		});
		tagList.forEach(tag -> {
			String[] split = tag.getName().toLowerCase().split(" ");
			for (int i = 0; i < split.length; i++) {
				if (val != 1) {
					tagsList.add(stem(split[i]));
				} else {
					tagsList.add(split[i]);
				}
			}
		});
		return tagsList;
	}

	private static void readName(String[] nameSplit) {

		nameSet = new ArrayList<String>();
		for (int i = 0; i < nameSplit.length; i++) {
			nameSet.add(stem(nameSplit[i]));
		}
	}

	public static boolean checkStopWord(String term) {

		for (int i = 0; i < stopWords.length; i++) {
			if (term.equalsIgnoreCase(stopWords[i]))
				return true;
		}
		return false;
	}

	public static String removePunctuations(String term) {

		boolean isModified = false;
		String[] punctuationMarks = { ".", ",", "?", ":", ";", "\'", "\"", "`", "-", "_", "{", "}", "[", "]", "(", ")",
				"'", "@", "#", "$", "%", "^", "&", "*" };
		String newTerm = null;
		for (int i = 0; i < punctuationMarks.length; i++) {
			if (term.contains(punctuationMarks[i])) {
				newTerm = "";
				String mark = punctuationMarks[i];
				List<String> tempTerm = Arrays.asList(term.split(""));
				List<String> tempList = new ArrayList<String>(tempTerm);
				for (int j = 0; j < tempList.size(); j++) {
					if (tempList.get(j).equalsIgnoreCase(mark)) {
						tempList.remove(j);
					}
				}
				for (String t : tempList) {
					newTerm += t;
				}
				term = newTerm;
				isModified = true;
			}
		}
		return isModified ? term : null;
	}

	public static String stem(String word) {
		return new PorterStemmer().stem(word);
	}
}
