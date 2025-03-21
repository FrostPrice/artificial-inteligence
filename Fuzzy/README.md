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

1. Guardians of the Galaxy - Score: 9.381866
2. Teenage Mutant Ninja Turtles - Score: 8.321616
3. The Hunger Games: Mockingjay - Part 2 - Score: 8.294137
4. Transformers: Age of Extinction - Score: 8.276879
5. Men in Black - Score: 8.256474
6. Interstellar - Score: 8.250001
7. Deadpool - Score: 8.250001
8. John Carter - Score: 8.25
9. Superman Returns - Score: 8.25
10. Man of Steel - Score: 8.25
