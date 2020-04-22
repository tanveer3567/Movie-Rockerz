package org.uta.datamining.spring2019;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

public class MovieDetailsService {

	public static MovieDetails getMovieDetails(int movieId) {

		String moviesDataSet = "/src/tmdb_5000_movies.csv";
		MovieDetails movieDetails = null;
		try {
			String property = System.getProperty("user.dir");
			FileReader filereader = new FileReader(new File(property + moviesDataSet));
			CSVReader csvReader = new CSVReader(filereader);
			String[] nextRecord;
			int k = 0;
			while ((nextRecord = csvReader.readNext()) != null) {
				if (k > 0) {
					int recordId = Integer.parseInt(nextRecord[3]);
					if (movieId == recordId) {
						String overview = nextRecord[7];
						List<String> tags = readTags(nextRecord[4]);
						List<String> genres = readTags(nextRecord[1]);
						BigInteger budget = BigInteger.valueOf(Long.parseLong(nextRecord[0]));
						BigInteger revenue = BigInteger.valueOf(Long.parseLong(nextRecord[12]));
						int runTime = Integer.parseInt(nextRecord[13]);
						Date releaseDate = new SimpleDateFormat("MM/DD/YYYY").parse(nextRecord[11]);
						String title = nextRecord[6];
						String url = nextRecord[2];
						double popularity = Double.parseDouble(nextRecord[8]);
						double avgVotes = Double.parseDouble(nextRecord[18]);
						int noOfVotes = Integer.parseInt(nextRecord[19]);
						movieDetails = new MovieDetails(movieId, budget, revenue, url, tags, genres, title, overview,
								runTime, releaseDate, popularity, noOfVotes, avgVotes);
						break;
					}
				}
				k++;
			}
			csvReader.close();
		} catch (Exception e) {
		}
		return movieDetails;
	}

	private static List<String> readTags(String nextRecord)
			throws JsonParseException, JsonMappingException, IOException {

		List<String> tags = new ArrayList<String>();
		String tagsJson = nextRecord;
		ObjectMapper mapper = new ObjectMapper();
		List<Tag> tagList = mapper.readValue(tagsJson, new TypeReference<List<Tag>>() {
		});
		tagList.forEach(tag -> tags.add(tag.getName()));
		return tags;
	}
}
