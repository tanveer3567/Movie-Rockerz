package org.uta.datamining.spring2019;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;

import org.apache.tomcat.util.buf.UDecoder;

import java.util.Objects;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

public class SearchAlgorithm {

	static List<String> docTerms = null;
	static LinkedHashMap<Integer, ArrayList<String>> documentMap = new LinkedHashMap<Integer, ArrayList<String>>();
	static LinkedHashSet<String> hashSet = new LinkedHashSet<String>();
	static LinkedHashMap<Integer, Double> finalVectorList = null;
	static LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>> documentPrimaryVectorMap = new LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>>();
	static LinkedHashMap<Integer, LinkedHashMap<Integer, Double>> documentFinalVectorMap = new LinkedHashMap<Integer, LinkedHashMap<Integer, Double>>();
	static PrintWriter printWriter = null;
	static LinkedHashMap<String, Integer> documentFrquencyMapOfTerms = new LinkedHashMap<String, Integer>();
	static LinkedHashMap<String, Integer> termFrequencyMapOfTerms = new LinkedHashMap<String, Integer>();
	static LinkedHashMap<String, Double> idfMapOfTerms = new LinkedHashMap<String, Double>();
	static int frquencyCounter = 0;
	static List<Integer> termFrequencyList = new ArrayList<Integer>();
	static List<Double> idfList = new ArrayList<Double>();
	static LinkedHashSet<String> queryHashSet = new LinkedHashSet<String>();
	static LinkedHashMap<Integer, ArrayList<Double>> queryVectorMap = new LinkedHashMap<Integer, ArrayList<Double>>();
	static LinkedHashMap<Integer, String> movieNameMap = new LinkedHashMap<Integer, String>();
	static List<String> tagsList = null;
	static ArrayList<String> nameSet = null;
	static LinkedHashMap<Integer, String> overviewMap = new LinkedHashMap<Integer, String>();
	static ArrayList<String> uniqueTermList = new ArrayList<String>();

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
			"we'll", "we're", "we've", "were", "what", "what's", "when", "when?s", "where", "where?s", "which", "while",
			"who", "who's", "whom", "why", "why's", "with", "would", "you", "you'd", "you'll", "you're", "you've",
			"your", "yours", "yourself", "yourselves" };

	public static void preLoad() throws FileNotFoundException {

		String moviesDataSet = "src/tmdb_5000_movies.csv";
		try {
			FileReader filereader = new FileReader(new File(moviesDataSet));
			CSVReader csvReader = new CSVReader(filereader);
			String[] nextRecord;
			int k = 0;
			while ((nextRecord = csvReader.readNext()) != null) {
				if (k > 0) {
					String name = nextRecord[6];
					String[] nameSplit = name.toLowerCase().split(" ");
					readName(nameSplit);
					String overview = nextRecord[7];
					String[] split = overview.toLowerCase().split(" ");
					readTags(nextRecord);
					docTerms = Arrays.asList(split);
					ArrayList<String> tempList = new ArrayList<String>(docTerms);
					tempList.addAll(tagsList);
					tempList.addAll(nameSet);
					documentMap.put(Integer.parseInt(nextRecord[3]), tempList);
					movieNameMap.put(Integer.parseInt(nextRecord[3]), name);
					overviewMap.put(Integer.parseInt(nextRecord[3]), overview);
				}
				k++;
			}
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
			hashSet.forEach(term -> uniqueTermList.add(term));
			documentAndTermFrequencyOfEachTerm();
			calculateIdf();
			createPrimaryDocumentVectors();
			createFinalDocumentVectors();
			csvReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static LinkedHashMap<CustomMap, String> search(String search) throws FileNotFoundException {

		TreeMap<CustomMap, String> query = getQuery(search);
		LinkedHashMap<CustomMap, String> finalResults = new LinkedHashMap<CustomMap, String>();
		query.forEach((customMap, overview) -> {
			if (customMap.getScore() > 0) {
				finalResults.put(customMap, overview);
			}
		});
		return finalResults;
	}

	private static void readName(String[] nameSplit) {

		nameSet = new ArrayList<String>();
		for (int i = 0; i < nameSplit.length; i++) {
			nameSet.add(nameSplit[i]);
		}
	}

	private static void readTags(String[] nextRecord) throws JsonParseException, JsonMappingException, IOException {

		tagsList = new ArrayList<String>();
		String tagsJson = nextRecord[4];
		ObjectMapper mapper = new ObjectMapper();
		List<Tag> tagList = mapper.readValue(tagsJson, new TypeReference<List<Tag>>() {
		});
		tagList.forEach(tag -> {
			String[] split = tag.getName().toLowerCase().split(" ");
			for (int i = 0; i < split.length; i++) {
				tagsList.add(split[i]);
			}
		});
	}

	private static TreeMap<CustomMap, String> getQuery(String query) throws FileNotFoundException {

		printWriter = new PrintWriter(new File("C:\\Users\\Tanveer\\Downloads\\tmdb-5000-movie-dataset\\Sample.txt"));
		ArrayList<String> queryList = new ArrayList<String>();
		LinkedHashMap<String, Integer> queryTermFrequencyMap = new LinkedHashMap<String, Integer>();
		LinkedHashMap<Integer, Double> queryVectorList = new LinkedHashMap<Integer, Double>();
		List<String> split = Arrays.asList(query.toLowerCase().split(" "));
		ArrayList<String> splitList = new ArrayList<String>(split);
		Comparator<CustomMap> scoreComparator = (e1, e2) -> {
			if (e1.getScore() > e2.getScore()) {
				return -1;
			} else {
				return 1;
			}
		};
		TreeMap<CustomMap, String> treeMap = new TreeMap<CustomMap, String>(scoreComparator);
		for (int i = 0; i < splitList.size(); i++) {
			if (checkStopWord(splitList.get(i))) {
				splitList.remove(i);
				i--;
			} else {
				String removePunctuations = removePunctuations(split.get(i));
				if (Objects.nonNull(removePunctuations))
					splitList.set(i, removePunctuations);
				queryHashSet.add(splitList.get(i));
				queryList.add(splitList.get(i));
			}
		}
		queryHashSet.forEach(queryTerm -> {
			int queryCounter = 0;
			for (int i = 0; i < queryList.size(); i++) {
				if (queryTerm.equalsIgnoreCase(queryList.get(i)))
					queryCounter++;
			}
			queryTermFrequencyMap.put(queryTerm, queryCounter);
		});
		LinkedHashMap<Integer, Double> queryFinalVectorMap = new LinkedHashMap<Integer, Double>();
		queryTermFrequencyMap.forEach((queryTerm, frequency) -> {
			queryFinalVectorMap.put(uniqueTermList.indexOf(queryTerm),
					frequency * idfList.get(uniqueTermList.indexOf(queryTerm)));
		});
		double y = 0;
		for (Entry<Integer, Double> entry : queryFinalVectorMap.entrySet()) {
			y += entry.getValue() * entry.getValue();
		}
		for (Entry<Integer, LinkedHashMap<Integer, Double>> entry : documentFinalVectorMap.entrySet()) {
			double x = 0;
			double val = 0;
			double cosineSimilarity = 0;
			for (Entry<Integer, Double> entryInner : entry.getValue().entrySet()) {
				x += entryInner.getValue() * entryInner.getValue();
			}
			for (Entry<Integer, Double> entryInner : queryFinalVectorMap.entrySet()) {
				if (entry.getValue().containsKey(entryInner.getKey())) {
					Double double1 = entry.getValue().get(entryInner.getKey());
					Double double2 = entryInner.getValue();
					val += double1 * double2;
				}
			}
			if (val > 0) {
				cosineSimilarity = val / (Math.sqrt(x) * Math.sqrt(y));
			}
			treeMap.put(new CustomMap(entry.getKey(), cosineSimilarity, movieNameMap.get(entry.getKey())),
					overviewMap.get(entry.getKey()));
		}
		return treeMap;
	}

	private static void documentAndTermFrequencyOfEachTerm() {

		hashSet.forEach(term -> {
			int documentFrequencyCounter = 0;
			int termFrequencyCounter = 0;
			for (Entry<Integer, ArrayList<String>> entry : documentMap.entrySet()) {
				boolean flag = false;
				for (String word : entry.getValue()) {
					if (word.equalsIgnoreCase(term)) {
						if (!flag) {
							documentFrequencyCounter++;
							flag = true;
						}
						termFrequencyCounter++;
					}
				}
			}
			documentFrquencyMapOfTerms.put(term, new Integer(documentFrequencyCounter));
			termFrequencyMapOfTerms.put(term, new Integer(termFrequencyCounter));
		});
	}

	private static void calculateIdf() {

		documentFrquencyMapOfTerms.forEach((term, value) -> {
			double divison = documentMap.size() / value;
			double idf = Math.log(divison);
			idfMapOfTerms.put(term, new Double(idf));
		});
	}

	public static void createPrimaryDocumentVectors() {

		termFrequencyMapOfTerms.forEach((term, value) -> {
			termFrequencyList.add(value);
		});

		idfMapOfTerms.forEach((term, value) -> {
			idfList.add(value);
		});

		for (Entry<Integer, ArrayList<String>> document : documentMap.entrySet()) {
			LinkedHashMap<Integer, Integer> vectorMap = new LinkedHashMap<Integer, Integer>();
			ArrayList<String> wordList = document.getValue();
			for (int i = 0; i < wordList.size(); i++) {
				if (vectorMap.containsKey(uniqueTermList.indexOf(wordList.get(i)))) {
					int intValue = vectorMap.get(uniqueTermList.indexOf(wordList.get(i))).intValue();
					vectorMap.put(uniqueTermList.indexOf(wordList.get(i)), ++intValue);
				} else {
					vectorMap.put(uniqueTermList.indexOf(wordList.get(i)), 1);
				}
			}
			documentPrimaryVectorMap.put(document.getKey(), vectorMap);
		}

	}

	public static void createFinalDocumentVectors() {

		for (Entry<Integer, LinkedHashMap<Integer, Integer>> entry : documentPrimaryVectorMap.entrySet()) {
			finalVectorList = new LinkedHashMap<Integer, Double>();
			LinkedHashMap<Integer, Integer> vectorMap = entry.getValue();
			vectorMap.forEach((id, value) -> {
				double x = value * idfList.get(id);
				double y = x / termFrequencyList.get(id);
				finalVectorList.put(id, y);
			});
			documentFinalVectorMap.put(entry.getKey(), finalVectorList);
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
}
