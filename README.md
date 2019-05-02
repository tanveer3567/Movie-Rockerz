# Movie Recommender System : Movie-Rockerz 

## Developing Basic search engine using vector space model:

### Data set: https://www.kaggle.com/tmdb/tmdb-movie-metadata

Columns in the data set used for search feature are

  1. Name of the movie.
  2. Overview of the movie.
  3. Tags of the movie.

The project provides a search box for input from the user. Once the search button is clicked search algorithm is run on the input data and related results will be shown to the user. The search algorithm is implemented in two phases.

### Phase 1 (Preload): This is done only once i.e. at the time of application boot up.

1. Name, overview and tags of all the movies from the data-set are fetched .

2. Then name, overview and tags of each movie are concatenated as one string and the entire string is split into each individual word and stored in a separate array for each movie and then the array is mapped to corresponding movie id.

```java
	String moviesDataSet = "/tmdb_5000_movies.csv";
		try {
			String property = System.getProperty("user.dir");
			FileReader filereader = new FileReader(new File(property + moviesDataSet));
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
					for (int i = 0; i < split.length; i++) {
						split[i] = stem(split[i]);
					}
					readTags(nextRecord);
					docTerms = Arrays.asList(split);
					ArrayList<String> tempList = new ArrayList<String>(docTerms);
					tempList.addAll(tagsList);
					tempList.addAll(nameSet);
					int record = Integer.parseInt(nextRecord[3]);
					documentMap.put(record, tempList);
					movieNameMap.put(record, name);
					overviewMap.put(record, overview);
					movieUrlMap.put(record, nextRecord[2]);
					tagsMap.put(record, tagsList);
				}
				k++;
			}
```

3. Then we remove stop words from each array by iterating over all the arrays.

4. Then we use porter’s steaming algorithm to get the steam form of each word by iterating over all the words in all the arrays.

```java
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
```

5. Then we form a list of all the unique terms and determine tf and idf values of these unique words.

```java
	hashSet.forEach(term -> uniqueTermList.add(term));
	private static void documentAndTermFrequencyOfEachTerm() {

		hashSet.forEach(term -> {
			int documentFrequencyCounter = 0;
			for (Entry<Integer, ArrayList<String>> entry : documentMap.entrySet()) {
				for (String word : entry.getValue()) {
					if (word.equalsIgnoreCase(term)) {
						documentFrequencyCounter++;
						break;
					}
				}
			}
			documentFrquencyMapOfTerms.put(term, new Integer(documentFrequencyCounter));
		});
	}

	private static void calculateIdf() {

		documentFrquencyMapOfTerms.forEach((term, value) -> {
			double divison = documentMap.size() / value;
			double idf = Math.log(divison);
			idfMapOfTerms.put(term, new Double(idf));
		});
	}
```

6. Then we give vector representation of each movie by calculating tf – idf value of each term corresponding to that movie.

```java
public static void createPrimaryDocumentVectors() throws FileNotFoundException {

		PrintWriter wr2 = new PrintWriter(new File(System.getProperty("user.dir") + "/idf.txt"));
		idfMapOfTerms.forEach((term, value) -> {
			idfList.add(value);
			wr2.write(term + ": " + value + "\n");
		});
		wr2.flush();
		wr2.close();

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
		PrintWriter wr3 = new PrintWriter(new File(System.getProperty("user.dir") + "/prime.txt"));
		documentPrimaryVectorMap.forEach((key, value) -> {
			String name = movieNameMap.get(key);
			value.forEach((id, score) -> {
				wr3.write("(" + name + ", ");
				wr3.write(uniqueTermList.get(id) + ")" + " : " + score + "\n");
			});
		});
		wr3.flush();
		wr3.close();
	}

	public static void createFinalDocumentVectors() throws FileNotFoundException {

		LinkedHashMap<Integer, LinkedHashMap<Integer, Double>> interMap = new LinkedHashMap<Integer, LinkedHashMap<Integer, Double>>();
		for (Entry<Integer, LinkedHashMap<Integer, Integer>> entry : documentPrimaryVectorMap.entrySet()) {
			finalVectorList = new LinkedHashMap<Integer, Double>();
			LinkedHashMap<Integer, Integer> vectorMap = entry.getValue();
			double normal = euclidianNormalization(vectorMap);
			LinkedHashMap<Integer, Double> map = new LinkedHashMap<Integer, Double>();
			vectorMap.forEach((id, value) -> {
				double x = value / normal;
				map.put(id, x);
				double y = x * idfList.get(id);
				finalVectorList.put(id, y);
			});
			documentFinalVectorMap.put(entry.getKey(), finalVectorList);
			interMap.put(entry.getKey(), map);
		}

		PrintWriter wr3 = new PrintWriter(new File(System.getProperty("user.dir") + "/Tf.txt"));
		interMap.forEach((key, value) -> {
			String name = movieNameMap.get(key);
			value.forEach((id, score) -> {
				wr3.write("(" + name + ", ");
				wr3.write(uniqueTermList.get(id) + ")" + " : " + score + "\n");
			});
		});
		wr3.flush();
		wr3.close();

		PrintWriter wr4 = new PrintWriter(new File(System.getProperty("user.dir") + "/final.txt"));
		documentFinalVectorMap.forEach((key, value) -> {
			String name = movieNameMap.get(key);
			value.forEach((id, score) -> {
				wr4.write("(" + name + ", ");
				wr4.write(uniqueTermList.get(id) + ")" + " : " + score + "\n");
			});
		});
		wr4.flush();
		wr4.close();
	}

	public static double euclidianNormalization(LinkedHashMap<Integer, Integer> vectorMap) {

		int sqr = 0;
		for (Entry<Integer, Integer> entry : vectorMap.entrySet()) {
			sqr = sqr + (entry.getValue() * entry.getValue());
		}
		return Math.sqrt(sqr);
	}
```
