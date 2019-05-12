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

		LinkedHashMap<Integer, LinkedHashMap<Integer, Double>> interMap = new LinkedHashMap<Integer, LinkedHashMap<Integer, 	                 Double>>();
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

### Phase 2 (Search): This is done for each search request.

1. We now convert search query into array of terms.

```java
	String[] split2 = query.toLowerCase().split(" ");
		for (int i = 0; i < split2.length; i++) {
			split2[i] = stem(split2[i]);
		}
```

2. Then we remove stop words from array by iterating it.

3. Then we use porter’s steaming algorithm to get the steam form of each word by iterating over all the words in all the arrays

```java
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
```
4. Now, we calculate tf of each term in the array.

```java
	queryHashSet.forEach(queryTerm -> {
			int queryCounter = 0;
			for (int i = 0; i < queryList.size(); i++) {
				if (queryTerm.equalsIgnoreCase(queryList.get(i)))
					queryCounter++;
			}
			queryTermFrequencyMap.put(queryTerm, queryCounter);
		});
```
4. Then we get the idf values from the phase 1 for each term in the search query.

5. Then we give vector representation of the search query by calculating tf-idf score.

```java
	queryTermFrequencyMap.forEach((queryTerm, frequency) -> {
			if (uniqueTermList.contains(queryTerm)) {
				queryFinalVectorMap.put(uniqueTermList.indexOf(queryTerm),
						frequency * idfList.get(uniqueTermList.indexOf(queryTerm)));
			}
		});
```

6. Then we compute the cosine similarity of the search query with respect to each document i.e. movie (name and overview).

7. We store all the non-zero cosine similarity scores and rank them in descending order and show them to the user.

```java

	Comparator<CustomMap> scoreComparator = (e1, e2) -> {
			if (e1.getScore() > e2.getScore()) {
				return -1;
			} else {
				return 1;
			}
		};
	TreeMap<CustomMap, String> treeMap = new TreeMap<CustomMap, String>(scoreComparator);
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
		treeMap.put(
				new CustomMap(entry.getKey(), cosineSimilarity, movieNameMap.get(entry.getKey()),
						movieUrlMap.get(entry.getKey()), tagsMap.get(entry.getKey())),
				overviewMap.get(entry.getKey()));
	}
```

## Developing basic classifier using Navie Bayes Classifier:

### Data set: https://www.kaggle.com/tmdb/tmdb-movie-metadata

Columns in the data set used for classifier feature are

  1. Name of the movie.
  2. Overview of the movie.
  3. Tags of the movie.
  4. Genre of the movie.

The project provides a text box for input from the user. Once the classify button is clicked classifier algorithm is executed on the input data and related results will be shown to the user. The classifier algorithm is implemented in two phases.

### Phase 1 (Training): This is done only once i.e. at the time of application boot up.

1. Name, overview and tags of all the movies from the data-set are fetched.

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
```

3. Then we remove stop words from each array by iterating over all the arrays.

4. Then we use porter’s steaming algorithm to get the steam form of each word by iterating over all the words in all the arrays.

```java
	documentMap.forEach((id, termList) -> {
				if (Objects.nonNull(termList)) {
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

5. Now, we retrieve genres of each movie and store them in each array. Then map the genre arrays to corresponding movieId.

6. We also get all the genres from the data-set and store them in a set.

```java
	documentMap.forEach((id, terms) -> {
				if (Objects.nonNull(terms)) {
					genreSet.forEach(genre -> {
						if (tagsMap.get(id).contains(genre)) {
							if (genreMap.containsKey(genre)) {
								genreMap.get(genre).addAll(terms);
							} else {
								genreMap.put(genre, terms);
							}
						}
					});
				}
			});
```

7. We not create a map containing genre as the key and list of terms appeared in the genre from the data-set.

8. Then we calculate the probability of genre.

```java
	genreSet.forEach(genre -> {
				List<Integer> idList = new ArrayList<Integer>();
				tagsMap.forEach((id, tagList) -> {
					if (tagList.contains(genre)) {
						idList.add(id);
					}
				});
				probabilityOfGenre.put(genre, (double) idList.size() / documentMap.size());
			});
```

9.  Now we calculate the term frequency of the each term in the data-set and then calculate probability of term give genre i.e P( T | G )using conditional probability formula (Note we apply smoothing here i.e. increment term frequency counter by 1 for each term).

```java
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
```

10. Now we store P( T | G) values in a map.

```java
	termCounterMap.forEach((key, value) -> {
				int a = value + 1;
				int b = genreMap.get(key.getGenre()).size();
				int c = hashSet.size();
				double result = (double) a / (double) (b + c);
				score.put(new ProbabilityOfTermGivenGenre(key.getTerm(), key.getGenre()), result);
			});
			csvReader.close();
```

### Phase 2 (Classify): This is done for each classify request.

1. We now convert search query into array of terms.

```java
	String[] split = query.split(" ");
	for (String tempTerm : split) {
			tempTerm = stem(tempTerm);
	}
```

2. Then we remove stop words from each array by iterating over it.

3. Then we use porter’s steaming algorithm to get the stem form of each word by iterating over all the words in the array.

```java
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
```

4. We now calculate the term frequency of each term and in the classify query.

5. Then we get the P(T | G) values from the phase 1 for each term in the classify query.

6. We now calculate P(G | T) value for each term using Bayer's Rule i.e.    

7. We know add score for each term in the classify query. Repeat, step 5, 6 for all genres.

8. We store now store top 3 scores and display the corresponding genres to the user.

```java
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

```
### Classifier Evaluation:

1. After creating document to terms map we now split the dataset into 60% training, 20% development, 20% testing.

```java
	public static void documentRandomizer() {

		Random random = new Random();
		List<Integer> keys = new ArrayList<Integer>(documentMap.keySet());
		int size = documentMap.size();
		while (documentMap.size() > size * 0.6) {
			Integer movieId = keys.get(random.nextInt(keys.size()));
			ArrayList<String> termList = documentMap.remove(movieId);
			if (Objects.nonNull(termList)) {
				trainingDocMap.put(movieId, termList);
			}
		}
		List<Integer> tempKeys = new ArrayList<Integer>(documentMap.keySet());
		size = documentMap.size();
		while (documentMap.size() > size * 0.5) {
			Integer movieId = keys.get(random.nextInt(tempKeys.size()));
			ArrayList<String> termList = documentMap.remove(movieId);
			if (Objects.nonNull(termList)) {
				developmentDocMap.put(movieId, termList);
			}
		}
	}	
```

2. Now, we apply phase 1 of the classifier and train the classifier.

3. Then we apply phase 2 of the classifer and classify each movie's description in the development and predict 1 genre.

5. Then we calculate F1 - measure for predicting 1 genre and tabulate the results.

4. We repeat step 3 for precting 2 genres, 3 genres, ... 22 genres.

```java
	public static void evaluate() {

		developmentDocMap.forEach((id, termList) -> {
			if (Objects.nonNull(termList)) {
				for (int i = 0; i < termList.size(); i++) {
					if (checkStopWord(termList.get(i))) {
						termList.remove(i);
						i--;
					} else {
						String removePunctuations = removePunctuations(termList.get(i));
						if (Objects.nonNull(removePunctuations))
							termList.set(i, removePunctuations);
					}
				}
			}
		});

		developmentDocMap.forEach((key, value) -> {
			String testString = StringUtils.join(value, " ");
			evaluateMap.put(key, classify(testString));
		});

		for (int i = 1; i <= genreSet.size(); i++) {
			evaluationForClassifer(evaluateMap,i, false);
		}

		LinkedHashMap<Integer, Double> avgFmeasureMap = new LinkedHashMap<Integer, Double>();
		for (int i = 1; i <= genreSet.size(); i++) {
			int size = 0;
			double fSum = 0;
			for (Statistic statistic : staticList) {
				if (statistic.getGenreCount() == i) {
					fSum += statistic.getfMeasure();
					size++;
				}
			}
			avgFmeasureMap.put(i, (double) fSum / (double) size);
		}

		avgFmeasureMap.forEach((key, value) -> {
			System.out.println(key + ": " + value);
		});
		fCount = Collections.max(avgFmeasureMap.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();

		documentMap.forEach((id, termList) -> {
			if (Objects.nonNull(termList)) {
				for (int i = 0; i < termList.size(); i++) {
					if (checkStopWord(termList.get(i))) {
						termList.remove(i);
						i--;
					} else {
						String removePunctuations = removePunctuations(termList.get(i));
						if (Objects.nonNull(removePunctuations))
							termList.set(i, removePunctuations);
					}
				}
			}
		});

		documentMap.forEach((key, value) -> {
			String testString = StringUtils.join(value, " ");
			testMap.put(key, classify(testString));
		});

		evaluationForClassifer(testMap, fCount, true);

		double fSum = 0;
		for (Statistic statistic : testList) {
			fSum += statistic.getfMeasure();
		}

		System.out.println((double) fSum / (double) testList.size());
	}
```

6. Then we calculate F1 - measure for predicting 2 genre, 3 genre,.... 22 genre anf tabulate the results.

7. We run 5 simulating of step 3 to 6 and get highest F1-meaure and its corresponding no of genres.

```java
	public static void evaluationForClassifer(LinkedHashMap<Integer, LinkedHashMap<String, Double>> map, int genreCount, boolean 	         test) {

		map.forEach((movieId, genreScore) -> {
			List<String> genreList = tagsMap.get(movieId);
			List<String> keys = new ArrayList<String>(genreScore.keySet());
			int trueP = 0;
			for (int i = 0; i < genreCount; i++) {
				if (Objects.nonNull(genreList) && !genreList.isEmpty() && Objects.nonNull(keys) && !keys.isEmpty()) {
					if (genreList.contains(keys.get(i))) {
						++trueP;
					}
				}
			}
			double precision = (double) trueP / (double) genreCount;
			double recall = (double) trueP / (double) genreList.size();
			double fMeasure = 0;
			if (precision > 0 && recall > 0) {
				fMeasure = (2 * precision * recall) / (precision + recall);
			}
			if (test) {
				testList.add(new Statistic(genreCount, movieId, genreScore, precision, recall, fMeasure));
			} else {
				staticList.add(new Statistic(genreCount, movieId, genreScore, precision, recall, fMeasure));
			}

		});
	}
```

8. We get highest F1- measure for predicting 4 genre. So, our final classifier will predict 4 genre.

## Developing basic content based recommender system

### Data set: https://www.kaggle.com/tmdb/tmdb-movie-metadata

Columns in the data set used for recommendation feature are

  1. Name of the movie.
  2. Overview of the movie.

The project provides a search box for input from the user. Once the search button is clicked search algorithm is run on the input data and related results will be shown to the user. Once the user clicks on a particular movie then

1. User will be able to see the details of the movie like name, genre, budget, revenue, tags, runtime, release date, popularity etc.

2. Then after that upto 20 movies are recommended to the user which are similar to the movie on whicj he click earlier.

3. How this happens is the description of the movie on which is clicked is fed to search algorith as a query

```java
	public ModelAndView getMovieDetails(@RequestParam(name = "movieId", required = true) String movieId)
			throws FileNotFoundException {

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
```

4. Then movies with highest cosine similarity are shown as recommended movies along with the details of the movies which is clicked        earlier.

5. Use of cookies to store moviedId on client's browser i.e. recommendation system tracks the last movie that user clicked and stroes it on browser.

6. When the user opens the website 20 movies will be recommended to user based on movie description of last seen movie.

```java
	Cookie[] cookies = httpRequest.getCookies();
		if (!(Objects.nonNull(cookies) && cookies.length > 0)) {
			Cookie cookie = new Cookie("recommendations", movieId);
			httpResponse.addCookie(cookie);
		}else {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equalsIgnoreCase("recommendations")) {
					cookie.setValue(movieId);
					httpResponse.addCookie(cookie);
				}
			}
		}
```


## Technology stack

Java 1.8, Spring boot 4, Thymleaf, Bootstrap, Apache Maven, Apache OpenNlp, AWS EC2, AWS S3, Spring tool suit 4

## How to use my code:

1. First of all your machine should have following softwares installed in it.\

	- Java 1.8
	- Eclipse or Spring tool suit.
	- Apache Maven
	- Spring Framework

2. Then import this project into the workspace, then do a Maven update project and  do a Maven clean install.

3. Then run the project as a spring boot application and this will generete a movie_cockerz-1.0.War in target folder of the project.

4. Go to http://localhost:8080 to access the application.

## How to deploy the code on a remote server

1. You should be familiar with AWS EC2 and S3 services offered by amazon.

2. First create a S3 storages and add movie_cockerz-1.0.War file which is generated in target forlder of the project in your loal machine when you run the project as spring boot project.

3. Then upadte the data-set.csv file to S3 storage and then a remote link gets generated

3. When you upload the war file to S3 it will give a remote link to the file.

4. Then create a Unix EC2 instance which comes with Java 1.8 preinstalled and connect to it.

5. Then execute the following comands

	- wget <war-remote-link>
	- wget <data-set.remote-link>
	- nohop java -jar movie-rokcerz-1.0.war
	- ctrl + z
	- bg
	
6. Then our deployment will start. You can monitor your deployment by typing "vi nohop.out" command in unix console. 

7. After deploying, type the following in your browser to access the application.

	http://Unix-Machine-name:8080

## References

### Search

1. https://nlp.stanford.edu/IR-book/pdf/06vect.pdf
2. https://kb.yoast.com/kb/list-stop-words/

## Classifier

1. https://nlp.stanford.edu/IR-book/pdf/13bayes.pdf
2. https://www.3pillarglobal.com/insights/document-classification-using-multinomial-naive-bayes-classifier
3. https://kb.yoast.com/kb/list-stop-words/

## Recommender

1. http://infolab.stanford.edu/~ullman/mmds/ch9.pdf
2. https://kb.yoast.com/kb/list-stop-words/

## Blogs:

1. [Search](https://shaiktanveerahmed1.wixsite.com/homepage/search-feature)
2. [Classifer](https://shaiktanveerahmed1.wixsite.com/homepage/classifier-feature)
3. [Recommender](https://shaiktanveerahmed1.wixsite.com/homepage/recommender-feayure)

## [Click](https://shaiktanveerahmed1.wixsite.com/homepage/datamining) for more details about the project.
