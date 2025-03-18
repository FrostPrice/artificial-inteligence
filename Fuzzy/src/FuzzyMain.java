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

		FuzzyVariable lowGenreMatch = new FuzzyVariable("Low Genre Match", 0, 0, 0.2f, 0.4f);
		FuzzyVariable mediumGenreMatch = new FuzzyVariable("Medium Genre Match", 0.2f, 0.4f, 0.6f, 0.8f);
		FuzzyVariable highGenreMatch = new FuzzyVariable("High Genre Match", 0.6f, 0.8f, 1.0f, 1.0f);

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
				try {
					voteAverageValue = Float.parseFloat(data[19]); // Correct column
					voteAverageGroup.fuzzify(voteAverageValue, fuzzyVariables);

					popularityValue = Float.parseFloat(data[9]); // Correct column
					popularityGroup.fuzzify(popularityValue, fuzzyVariables);
				} catch (NumberFormatException e) {
					continue; // Skip rows with invalid numerical data
				}

				float genreValue = computeGenreScore(data[2]); // Process genres correctly
				genreMatchGroup.fuzzify(genreValue, fuzzyVariables);

				// Apply AND rules
				applyAndRule(fuzzyVariables, "High Vote Average", "High Popularity", "Very Interesting");
				applyAndRule(fuzzyVariables, "Medium Vote Average", "High Popularity", "Very Interesting");
				applyAndRule(fuzzyVariables, "High Vote Average", "Medium Popularity", "Average Interesting");
				applyAndRule(fuzzyVariables, "Low Vote Average", "High Popularity", "Average Interesting");
				applyAndRule(fuzzyVariables, "Medium Vote Average", "Medium Popularity", "Average Interesting");
				applyAndRule(fuzzyVariables, "Low Vote Average", "Low Popularity", "Low Interesting");

				applyAndRule(fuzzyVariables, "High Genre Match", "High Vote Average", "Very Interesting");
				applyAndRule(fuzzyVariables, "Medium Genre Match", "Medium Popularity", "Average Interesting");
				applyAndRule(fuzzyVariables, "Low Genre Match", "Low Popularity", "Low Interesting");

				// Apply OR rules
				applyOrRule(fuzzyVariables, "High Vote Average", "High Popularity", "Very Interesting");
				applyOrRule(fuzzyVariables, "Medium Vote Average", "Medium Popularity", "Average Interesting");
				applyOrRule(fuzzyVariables, "Low Vote Average", "Low Popularity", "Low Interesting");

				float lowInteresting = fuzzyVariables.getOrDefault("Low Interesting", 0.0f);
				float averageInteresting = fuzzyVariables.getOrDefault("Average Interesting", 0.0f);
				float veryInteresting = fuzzyVariables.getOrDefault("Very Interesting", 0.0f);

				float score = (lowInteresting * 1.5f + averageInteresting * 7.0f + veryInteresting * 9.5f)
						/ (lowInteresting + averageInteresting + veryInteresting);

				System.out
						.println("Low Interesting: " + lowInteresting + " Average Interesting: " + averageInteresting
								+ " Very Interesting: " + veryInteresting);
				System.out.println("VoteAverage: " + voteAverageValue + " PopularityValue: " + popularityValue
						+ " genreValue: " + genreValue + " -> " + score);

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
					(i + 1) + ". " + movieScores.get(i).getTitle() + " - Score: " + movieScores.get(i).getScore());
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
		String[] targetGenres = { "Action", "Adventure", "Science Fiction" };

		// Count how many of the target genres appear in the movie's genre string
		int matchCount = 0;
		for (String target : targetGenres) {
			if (genres.contains(target)) {
				matchCount++;
			}
		}

		// Normalize the score to the range [0,1] based on the number of matching genres
		// Avoid division by zero and ensure a valid result
		return (matchCount > 0) ? (matchCount / (float) targetGenres.length) : 0.0f;
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