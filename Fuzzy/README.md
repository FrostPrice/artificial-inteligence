# Fuzzy Logic Movie Scoring

This project uses fuzzy logic to score movies based on various attributes such as vote average, popularity, and genre match. The fuzzy logic system categorizes these attributes into different fuzzy variables and applies fuzzy rules to compute a final score for each movie.

## Project Structure

- `FuzzyVariable.java`: Defines the `FuzzyVariable` class, which represents a fuzzy variable with a name and membership function parameters.
- `VariableGroup.java`: Defines the `VariableGroup` class, which manages a group of `FuzzyVariable` instances and provides methods to fuzzify values.
- `FuzzyMain.java`: The main class that reads movie data from a CSV file, fuzzifies the attributes, applies fuzzy rules, and computes the final scores for the movies.

## Results for Top 10 Movies with the Genres: Action, Adventure, and Science Fiction

Top 10 Movies with Best Score:

1. Interstellar - Score: 8.25
2. Guardians of the Galaxy - Score: 8.047424
3. Deadpool - Score: 7.714286
4. Minions - Score: 7.685653
5. Mad Max: Fury Road - Score: 6.663608
6. Whiplash - Score: 6.136136
7. The Dark Knight - Score: 6.100345
8. Jurassic World - Score: 6.071035
9. Inception - Score: 5.9646378
10. Big Hero 6 - Score: 5.847973
