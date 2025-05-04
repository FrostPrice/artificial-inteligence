import pandas as pd

# Funcoes de similaridade
def rating_similarity(a, b):
    return max(0, 1 - abs(a - b) / 1000)

def categorical_similarity(a, b):
    return 1 if a == b else 0

def calculate_similarity(row, new_case, weights):
    sim = 0
    total_weight = sum(weights.values())

    sim += rating_similarity(row["white_rating"], new_case["white_rating"]) * weights["white_rating"]
    sim += rating_similarity(row["black_rating"], new_case["black_rating"]) * weights["black_rating"]
    sim += categorical_similarity(row["opening_name"], new_case["opening_name"]) * weights["opening_name"]
    sim += categorical_similarity(row["increment_code"], new_case["increment_code"]) * weights["increment_code"]

    return sim / total_weight

def get_input(prompt, cast_func, default):
    user_input = input(f"{prompt} (default: {default}): ").strip()
    return cast_func(user_input) if user_input else default

def cli_rbc():
    print("=== Configurar novo caso ===")
    new_case = {
        "white_rating": get_input("Digite o rating do jogador branco", int, 1850),
        "black_rating": get_input("Digite o rating do jogador preto", int, 1900),
        "opening_name": get_input("Digite o nome da abertura", str, "Sicilian Defense"),
        "increment_code": get_input("Digite o código de incremento", str, "10+0")
    }

    print("\n=== Configurar pesos ===")
    weights = {
        "white_rating": get_input("Peso para rating do branco", float, 1.0),
        "black_rating": get_input("Peso para rating do preto", float, 1.0),
        "opening_name": get_input("Peso para nome da abertura", float, 0.4),
        "increment_code": get_input("Peso para código de incremento", float, 0.2)
    }

    df = pd.read_csv("games.csv")
    df = df[["white_rating", "black_rating", "opening_name", "increment_code", "winner"]].dropna()
    df["similarity"] = df.apply(lambda row: calculate_similarity(row, new_case, weights), axis=1)
    df["similarity (%)"]= (df["similarity"] * 100).round(2)
    df_sorted = df.sort_values(by="similarity", ascending=False).reset_index(drop=True)

    print("\nNovo caso:")
    print(new_case)
    print("\nTop 5 casos mais similares:")
    print(df_sorted.head(5)[["white_rating", "black_rating", "opening_name", "increment_code", "winner", "similarity (%)"]])

    top_k = 5
    predicted = df_sorted.head(top_k)["winner"].mode()[0]
    print(f"\nResultado previsto com base nos {top_k} mais similares: {predicted.upper()}")

if __name__ == "__main__":
    cli_rbc()
