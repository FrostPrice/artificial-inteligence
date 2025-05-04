# ♟️ RBC Chess Result Predictor

A Case-Based Reasoning (RBC) system that predicts the likely outcome of a chess match based on historical cases from the [Kaggle Chess Games dataset](https://www.kaggle.com/datasets/datasnaek/chess/data).

---

## 🧠 Project Summary

This application uses **Case-Based Reasoning** to retrieve similar chess games from a dataset and predict the result of a new match based on historical patterns.

- Input: A new chess match scenario (ratings, opening, time format)
- Output: Similar historical cases ranked by similarity and predicted winner

---

## 📦 Features

- ✅ Input new match data
- ✅ Customizable attribute weights
- ✅ Outputs top similar matches with similarity %
- ✅ Uses a clean and interpretable similarity model
- ✅ Implemented in Python with pandas
- ✅ Optionally, a GUI via Streamlit (if enabled)

---

## 📊 Atributes Used

- white_rating (numeric): Rating of the player with the white pieces (Its a strong metric, and determines how good the player is)
- black_rating (numeric): Rating of the player with the black pieces (Its a strong metric, and determines how good the player is)
- opening_name (categorical): Name of the opening used in the match (The opening influences the style of the game, but doesn't determine the winner, usually a moderated metric)
- increment_code (categorical): Time increment per move (Complementary metric, but not very relevant. )
  - "3+2" (3 minutes + 2 seconds per move)
  - "10+0" (10 minutes, no increment)
  - "15+10" (15 minutes + 10 seconds per move)
  - "1+0" (1 minute, no increment)
  - ...
- winner (label): Result of the match (white / black / draw)

## ⚖️ Attribute Weights (default)

- white_rating: 1.0
- black_rating: 1.0
- opening_name: 0.4
- increment_code: 0.2

Note: The user can adjust these weights via the Streamlit interface.

## 📐 Similarity Metrics

- For numeric attributes:
  similarity = max(0, 1 - abs(a - b) / 1000)

- For categorical attributes:
  similarity = 1 if equal, 0 if different

## 🧮 Global Similarity

```python
similarity = (sim1 * weight1 + sim2 * weight2 + ...) / sum_of_weights
```

Where:

- sim1, sim2, ... are the similarities of each attribute
- weight1, weight2, ... are the weights of each attribute
- sum_of_weights is the total of all weights
- The result is a value between 0 and 1, where 1 means identical cases

## 🔍 Retrieval Process

1. The user inputs a new case.
2. The system calculates similarity with all cases in the database.
3. Orders the cases by highest similarity.
4. Returns the K most similar and applies voting to predict the result.

---

## 🚀 Getting Started

### 1. Install Python (>= 3.8)

Make sure you have Python installed (recommended: Arch Linux users install via `pacman`):

```bash
sudo pacman -S python
```

### 2. Create a Virtual Environment

```bash
python -m venv .venv
source .venv/bin/activate
```

### 3. Install Required Packages

```bash
pip install -r requirements.txt
```

### 4. Run the Project (CLI)

```bash
python main.py
```

### 5. Run the Project (GUI)

```bash
streamlit run streamlit_app.py
```

---

## Dataset Info

- Source: Kaggle Chess Dataset
- Includes 50+ sample games with: ratings, openings, time controls, and results

## License

MIT License – Free for academic and educational use.
