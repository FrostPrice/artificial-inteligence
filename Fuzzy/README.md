# Fuzzy Logic Movie Scoring

This project uses fuzzy logic to score movies based on various attributes such as vote average, popularity, and genre match. The fuzzy logic system categorizes these attributes into different fuzzy variables and applies fuzzy rules to compute a final score for each movie.

## Project Structure

- `FuzzyVariable.java`: Defines the `FuzzyVariable` class, which represents a fuzzy variable with a name and membership function parameters.
- `VariableGroup.java`: Defines the `VariableGroup` class, which manages a group of `FuzzyVariable` instances and provides methods to fuzzify values.
- `FuzzyMain.java`: The main class that reads movie data from a CSV file, fuzzifies the attributes, applies fuzzy rules, and computes the final scores for the movies.

## Results for Top 10 Movies with the Genres: Action, Adventure, and Science Fiction

Top 10 Movies with Best Score:

1. Guardians of the Galaxy - Score: 8.9
2. Interstellar - Score: 8.5
3. Inception - Score: 8.25
4. The Empire Strikes Back - Score: 8.25
5. Star Wars - Score: 8.25
6. Star Wars: Clone Wars (Volume 1) - Score: 8.25
7. Deadpool - Score: 8.0
8. Return of the Jedi - Score: 7.89
9. The Lord of the Rings: The Fellowship of the Ring - Score: 7.67
10. The Lord of the Rings: The Return of the King - Score: 7.65
