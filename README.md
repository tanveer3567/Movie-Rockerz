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
```

5. Then we form a list of all the unique terms and determine tf and idf values of these unique words.

6. Then we give vector representation of each movie by calculating tf – idf value of each term corresponding to that movie.
