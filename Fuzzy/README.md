# Fuzzy Logic Movie Scoring

This project uses fuzzy logic to score movies based on various attributes such as vote average, popularity, and genre match. The fuzzy logic system categorizes these attributes into different fuzzy variables and applies fuzzy rules to compute a final score for each movie.

## Project Structure

- `FuzzyVariable.java`: Defines the `FuzzyVariable` class, which represents a fuzzy variable with a name and membership function parameters.
- `VariableGroup.java`: Defines the `VariableGroup` class, which manages a group of `FuzzyVariable` instances and provides methods to fuzzify values.
- `FuzzyMain.java`: The main class that reads movie data from a CSV file, fuzzifies the attributes, applies fuzzy rules, and computes the final scores for the movies.

## Movie types

- Action
- Adventure
- Fantasy
- Science Fiction
- Crime
- Drama
- Thriller
- Animation
- Family
- Western
- Comedy
- Romance
- Horror
- Mystery
- History
- War
- Music
- Documentary
- Foreign
- TV
- Movie

## Results for Top 10 Movies with the Genres: Action, Adventure, and Science Fiction

Top 10 Movies with Best Score:

1. Guardians of the Galaxy - Score: 9.262525
2. Interstellar - Score: 8.5
3. Deadpool - Score: 8.5
4. After Earth - Score: 8.25
5. Teenage Mutant Ninja Turtles - Score: 8.25
6. Fantastic Four - Score: 8.25
7. Men in Black - Score: 8.25
8. X-Men - Score: 8.25
9. Return of the Jedi - Score: 8.184211
10. Ant-Man - Score: 8.16137
