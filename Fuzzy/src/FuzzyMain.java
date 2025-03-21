import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FuzzyMain {
	public static void main(String[] args) {

		FuzzyVariable lowVoteAverage = new FuzzyVariable("Low Vote Average", 0, 0, 3, 5);
		FuzzyVariable mediumVoteAverage = new FuzzyVariable("Medium Vote Average", 3, 5, 7, 8);
		FuzzyVariable highVoteAverage = new FuzzyVariable("High Vote Average", 7, 8, 10, 10);

		FuzzyVariable lowPopularity = new FuzzyVariable("Low Popularity", 0, 0, 100, 500);
		FuzzyVariable mediumPopularity = new FuzzyVariable("Medium Popularity", 100, 500, 800, 1000);
		FuzzyVariable highPopularity = new FuzzyVariable("High Popularity", 800, 1000, 2000, 2000);

		FuzzyVariable lowGenreMatch = new FuzzyVariable("Low Genre Match", -999, 0, 0.2f, 0.4f);
		FuzzyVariable mediumGenreMatch = new FuzzyVariable("Medium Genre Match", 0.2f, 0.4f, 0.6f, 0.8f);
		FuzzyVariable highGenreMatch = new FuzzyVariable("High Genre Match", 0.6f, 0.8f, 1.0f, 1.0f);

		FuzzyVariable lowRevenue = new FuzzyVariable("Low Revenue", 0, 0, 20000000, 50000000);
		FuzzyVariable mediumRevenue = new FuzzyVariable("Medium Revenue", 20000000, 50000000, 93000000, 150000000);
		FuzzyVariable highRevenue = new FuzzyVariable("High Revenue", 93000000,
				150000000, 1000000000, 3000000000L); // The L defines long number

		FuzzyVariable lowVoteCount = new FuzzyVariable("Low Vote Count", 0, 0, 500, 2000);
		FuzzyVariable mediumVoteCount = new FuzzyVariable("Medium Vote Count", 500, 2000, 5000, 10000);
		FuzzyVariable highVoteCount = new FuzzyVariable("High Vote Count", 5000, 10000, 20000, 30000);

		FuzzyVariable shortRuntime = new FuzzyVariable("Short Runtime", 0, 0, 80, 95);
		FuzzyVariable mediumRuntime = new FuzzyVariable("Medium Runtime", 80, 95, 110, 120);
		FuzzyVariable longRuntime = new FuzzyVariable("Long Runtime", 110, 120, 20, 340);

		VariableGroup voteAverageGroup = new VariableGroup();
		voteAverageGroup.add(lowVoteAverage);
		voteAverageGroup.add(mediumVoteAverage);
		voteAverageGroup.add(highVoteAverage);

		VariableGroup popularityGroup = new VariableGroup();
		popularityGroup.add(lowPopularity);
		popularityGroup.add(mediumPopularity);
		popularityGroup.add(highPopularity);

		VariableGroup genreMatchGroup = new VariableGroup();
		genreMatchGroup.add(lowGenreMatch);
		genreMatchGroup.add(mediumGenreMatch);
		genreMatchGroup.add(highGenreMatch);

		VariableGroup revenueGroup = new VariableGroup();
		revenueGroup.add(lowRevenue);
		revenueGroup.add(mediumRevenue);
		revenueGroup.add(highRevenue);

		VariableGroup voteCountGroup = new VariableGroup();
		voteCountGroup.add(lowVoteCount);
		voteCountGroup.add(mediumVoteCount);
		voteCountGroup.add(highVoteCount);

		VariableGroup runtimeGroup = new VariableGroup();
		runtimeGroup.add(shortRuntime);
		runtimeGroup.add(mediumRuntime);
		runtimeGroup.add(longRuntime);

		VariableGroup interestGroup = new VariableGroup();
		interestGroup.add(new FuzzyVariable("Low Interesting", 0, 0, 3, 6));
		interestGroup.add(new FuzzyVariable("Average Interesting", 5, 7, 8, 10));
		interestGroup.add(new FuzzyVariable("Very Interesting", 7, 9, 10, 10));

		List<MovieScore> movieScores = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader("movie_dataset.csv"))) {
			String header = reader.readLine(); // Read the first line (headers)
			String[] splitHeader = header.split(",");
			for (int i = 0; i < splitHeader.length; i++) {
				System.out.println(i + " " + splitHeader[i]);
			}

			String line;
			while ((line = reader.readLine()) != null) {
				String[] data = parseCSVLine(line);
				if (data.length < 20) {
					continue; // Skip incomplete rows
				}

				String movieTitle = data[7];

				HashMap<String, Float> fuzzyVariables = new HashMap<>();

				float voteAverageValue;
				float popularityValue;
				float revenueValue;
				float voteCountValue;
				float runtimeValue;
				try {
					voteAverageValue = Float.parseFloat(data[19]);
					voteAverageGroup.fuzzify(voteAverageValue, fuzzyVariables);

					popularityValue = Float.parseFloat(data[9]);
					popularityGroup.fuzzify(popularityValue, fuzzyVariables);

					revenueValue = Float.parseFloat(data[13]);
					revenueGroup.fuzzify(revenueValue, fuzzyVariables);

					voteCountValue = Float.parseFloat(data[20]);
					voteCountGroup.fuzzify(voteCountValue, fuzzyVariables);

					runtimeValue = Float.parseFloat(data[14]);
					runtimeGroup.fuzzify(runtimeValue, fuzzyVariables);
				} catch (NumberFormatException e) {
					continue; // Skip rows with invalid numerical data
				}

				float genreValue = computeGenreScore(data[2]); // Process genres correctly
				genreMatchGroup.fuzzify(genreValue, fuzzyVariables);

				// * AND Rules
				// Vote Average and Vote Count = Popularity
				applyAndRule(fuzzyVariables, "High Vote Average", "High Vote Count", "High Popularity");
				applyAndRule(fuzzyVariables, "Medium Vote Average", "High Vote Count", "Medium Popularity");
				applyAndRule(fuzzyVariables, "Low Vote Average", "High Vote Count", "Medium Popularity");

				applyAndRule(fuzzyVariables, "High Vote Average", "Medium Vote Count", "High Popularity");
				applyAndRule(fuzzyVariables, "Medium Vote Average", "Medium Vote Count", "Medium Popularity");
				applyAndRule(fuzzyVariables, "Low Vote Average", "Medium Vote Count", "Medium Popularity");

				applyAndRule(fuzzyVariables, "High Vote Average", "Low Vote Count", "Medium Popularity");
				applyAndRule(fuzzyVariables, "Medium Vote Average", "Low Vote Count", "Low Popularity");
				applyAndRule(fuzzyVariables, "Low Vote Average", "Low Vote Count", "Low Popularity");

				// Revenue and Popularity = Popularity
				applyAndRule(fuzzyVariables, "High Revenue", "High Popularity", "High Popularity");
				applyAndRule(fuzzyVariables, "Medium Revenue", "High Popularity", "High Popularity");
				applyAndRule(fuzzyVariables, "Low Revenue", "High Popularity", "Medium Popularity");

				applyAndRule(fuzzyVariables, "High Revenue", "Medium Popularity", "High Popularity");
				applyAndRule(fuzzyVariables, "Medium Revenue", "Medium Popularity", "Medium Popularity");
				applyAndRule(fuzzyVariables, "Low Revenue", "Medium Popularity", "Medium Popularity");

				applyAndRule(fuzzyVariables, "High Revenue", "Low Popularity", "Medium Popularity");
				applyAndRule(fuzzyVariables, "Medium Revenue", "Low Popularity", "Medium Popularity");
				applyAndRule(fuzzyVariables, "Low Revenue", "Low Popularity", "Low Popularity");

				// Genre Match and Popularity = Insteresting
				applyAndRule(fuzzyVariables, "High Genre Match", "High Popularity", "Very Interesting");
				applyAndRule(fuzzyVariables, "Medium Genre Match", "High Popularity", "Very Interesting");
				applyAndRule(fuzzyVariables, "Low Genre Match", "High Popularity", "Average Interesting");

				applyAndRule(fuzzyVariables, "High Genre Match", "Medium Popularity", "Very Interesting");
				applyAndRule(fuzzyVariables, "Medium Genre Match", "Medium Popularity", "Average Interesting");
				applyAndRule(fuzzyVariables, "Low Genre Match", "Medium Popularity", "Low Interesting");

				applyAndRule(fuzzyVariables, "High Genre Match", "Low Popularity", "Average Interesting");
				applyAndRule(fuzzyVariables, "Medium Genre Match", "Low Popularity", "Low Interesting");
				applyAndRule(fuzzyVariables, "Low Genre Match", "Low Popularity", "Low Interesting");

				// Genre Match and Runtime = Interesting
				applyAndRule(fuzzyVariables, "High Genre Match", "Long Runtime", "Very Interesting");
				applyAndRule(fuzzyVariables, "Medium Genre Match", "Long Runtime", "Very Interesting");
				applyAndRule(fuzzyVariables, "Low Genre Match", "Long Runtime", "Average Interesting");

				applyAndRule(fuzzyVariables, "High Genre Match", "Medium Runtime", "Very Interesting");
				applyAndRule(fuzzyVariables, "Medium Genre Match", "Medium Runtime", "Average Interesting");
				applyAndRule(fuzzyVariables, "Low Genre Match", "Medium Runtime", "Low Interesting");

				applyAndRule(fuzzyVariables, "High Genre Match", "Short Runtime", "Average Interesting");
				applyAndRule(fuzzyVariables, "Medium Genre Match", "Short Runtime", "Low Interesting");
				applyAndRule(fuzzyVariables, "Low Genre Match", "Short Runtime", "Low Interesting");

				float lowInteresting = fuzzyVariables.getOrDefault("Low Interesting", 0.0f);
				float averageInteresting = fuzzyVariables.getOrDefault("Average Interesting", 0.0f);
				float veryInteresting = fuzzyVariables.getOrDefault("Very Interesting", 0.0f);

				float score = (lowInteresting * 1.5f + averageInteresting * 7.0f + veryInteresting * 9.5f)
						/ (lowInteresting + averageInteresting + veryInteresting);

				System.out
						.println("Low Interesting: " + lowInteresting + " Average Interesting: " +
								averageInteresting
								+ " Very Interesting: " + veryInteresting);
				System.out.println("VoteAverage: " + voteAverageValue + " Popularity: " +
						popularityValue
						+ " Genre: " + genreValue + " Revenue: " + revenueValue + " VoteCount: " +
						voteCountValue
						+ " Runtime: " + runtimeValue + " -> " + score);

				if (!Float.isNaN(score)) {
					movieScores.add(new MovieScore(movieTitle, score));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Sort movies in descending order of score
		movieScores.sort((m1, m2) -> Float.compare(m2.getScore(), m1.getScore()));

		// Print top 10 movies
		System.out.println("\nTop 10 Movies with Best Score:");
		for (int i = 0; i < Math.min(10, movieScores.size()); i++) {
			System.out.println(
					(i + 1) + ". " + movieScores.get(i).getTitle() + " - Score: "
							+ movieScores.get(i).getScore());
		}
	}

	private static void applyAndRule(HashMap<String, Float> variables, String var1, String var2, String resultVar) {
		float value = Math.min(variables.getOrDefault(var1, 0.0f), variables.getOrDefault(var2, 0.0f));
		variables.put(resultVar, Math.max(variables.getOrDefault(resultVar, 0.0f), value));
	}

	private static void applyOrRule(HashMap<String, Float> variables, String var1, String var2, String resultVar) {
		float value = Math.max(variables.getOrDefault(var1, 0.0f), variables.getOrDefault(var2, 0.0f));
		variables.put(resultVar, Math.max(variables.getOrDefault(resultVar, 0.0f), value));
	}

	private static float computeGenreScore(String genres) {
		if (genres == null || genres.isEmpty())
			return 0.0f;

		// List of target genres we want to consider
		String[] targetGenres = { "Adventure", "Action", "Science Fiction" };

		// List of genres that the user dislikes
		String[] dislikedGenres = { "Horror", "Thriller", "Documentary", "Romance" };

		// Count how many of the target genres appear in the movie's genre string
		int matchCount = 0;
		for (String target : targetGenres) {
			if (genres.contains(target)) {
				matchCount++;
			}
		}

		// Penalize movies with disliked genres
		for (String disliked : dislikedGenres) {
			if (genres.contains(disliked)) {
				matchCount--;
			}
		}

		// Normalize the score to the range [-1,1] based on the number of matching
		// genres
		return matchCount / (float) targetGenres.length;
	}

	private static String[] parseCSVLine(String line) {
		return line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // Handle quoted commas
	}
}

class MovieScore {
	private final String title;
	private final float score;

	public MovieScore(String title, float score) {
		this.title = title;
		this.score = score;
	}

	public String getTitle() {
		return title;
	}

	public float getScore() {
		return score;
	}
}