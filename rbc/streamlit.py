import streamlit as st
import pandas as pd

# --- Funções de similaridade ---
def rating_similarity(a, b):
    return max(0, 1 - abs(a - b) / 1000)  # Faixa típica de rating no xadrez

def categorical_similarity(a, b):
    return 1 if a == b else 0  # Igual = 100%, diferente = 0%

def calculate_similarity(row, new_case, weights):
    sim = 0
    total_weight = sum(weights.values())

    sim += rating_similarity(row["white_rating"], new_case["white_rating"]) * weights["white_rating"]
    sim += rating_similarity(row["black_rating"], new_case["black_rating"]) * weights["black_rating"]
    sim += categorical_similarity(row["opening_name"], new_case["opening_name"]) * weights["opening_name"]
    sim += categorical_similarity(row["increment_code"], new_case["increment_code"]) * weights["increment_code"]

    # Peso baseado na importância do tipo de vitória
    vw_row = victory_status_weights.get(row["victory_status"], 0)
    vw_new = victory_status_weights.get(new_case["victory_status"], 0)
    victory_sim = 1 - abs(vw_row - vw_new)  # Quanto mais próximos os pesos, maior a similaridade
    sim += victory_sim * weights["victory_status"]

    return sim / total_weight

# --- Carregar dados ---
df = df = pd.read_csv("https://raw.githubusercontent.com/FrostPrice/artificial-inteligence/refs/heads/main/rbc/games.csv")
df = df[["white_rating", "black_rating", "opening_name", "increment_code", "victory_status", "winner"]].dropna()
df = df.copy()

victory_status_weights = {
    "checkmate": 1.0,
    "resignation": 0.8,
    "timeout": 0.6,
    "draw": 0.4,
    "abandoned": 0.2
}


# --- Interface Streamlit ---
st.title("RBC - Previsão de Resultado em Partidas de Xadrez")

# --- Seção de Modelagem ---
with st.expander("📘 Documentação da Modelagem do RBC"):
    st.markdown("""
    **Atributos utilizados**:
    - `white_rating` e `black_rating`: Representam a habilidade dos jogadores (numéricos).
    - `opening_name`: Representa a abertura jogada, que pode influenciar o resultado (categórico).
    - `increment_code`: Representa o controle de tempo, impactando o desempenho dos jogadores (categórico).
    - `victory_status`: Tipo de finalização do jogo, como `checkmate`, `resignation`, `timeout` (categórico). Vitórias por checkmate e resignation muitas vezes indicam partidas em que um lado claramente dominou.

    **Pesos (default)**:
    - Ratings: 1.0 cada (influência alta esperada).
    - Abertura: 0.4 (moderada influência).
    - Tempo: 0.2 (influência leve).
    - Tipo de Vitória: 0.3 (influência leve, mas reflete estilo/tendência da partida).

    **Métricas de Similaridade**:
    - Atributos numéricos: `1 - |a - b| / 1000` (quanto mais próximos, maior similaridade).
    - Atributos categóricos: `1 se iguais, 0 se diferentes`.

    **Justificativa**:
    - Ratings afetam diretamente o resultado, por isso recebem maior peso.
    - Abertura e incremento têm influência indireta, portanto peso menor.
    - Tipo de vitória pode refletir tendências de jogo e foi incluído como categoria auxiliar.
    """)

# --- Entrada de dados ---
st.sidebar.header("🔍 Parâmetros do Novo Caso")
white_rating = st.sidebar.slider("White Rating", 784, 2800, 1850)
black_rating = st.sidebar.slider("Black Rating", 789, 2800, 1900)
opening = st.sidebar.selectbox("Opening", sorted(df["opening_name"].unique()))
increment = st.sidebar.selectbox("Increment Code", sorted(df["increment_code"].unique()))
victory_status = st.sidebar.selectbox("Victory Status", sorted(df["victory_status"].unique()))

st.sidebar.header("⚖️ Pesos dos Atributos")
w_white = st.sidebar.slider("Peso: White Rating", 0.0, 2.0, 1.0)
w_black = st.sidebar.slider("Peso: Black Rating", 0.0, 2.0, 1.0)
w_opening = st.sidebar.slider("Peso: Opening Name", 0.0, 2.0, 0.4)
w_increment = st.sidebar.slider("Peso: Increment Code", 0.0, 2.0, 0.2)
w_victory_status = st.sidebar.slider("Peso: Victory Status", 0.0, 2.0, 0.3)

# Novo caso e pesos
new_case = {
    "white_rating": white_rating,
    "black_rating": black_rating,
    "opening_name": opening,
    "increment_code": increment,
    "victory_status": victory_status
}
weights = {
    "white_rating": w_white,
    "black_rating": w_black,
    "opening_name": w_opening,
    "increment_code": w_increment,
    "victory_status": w_victory_status
}

# Calcular similaridade
df["similarity"] = df.apply(lambda row: calculate_similarity(row, new_case, weights), axis=1)
df["similarity(%)"] = (df["similarity"] * 100).round(2)
df_sorted = df.sort_values(by="similarity", ascending=False).reset_index(drop=True)

# Mostrar resultado
st.subheader("🔎 Novo Caso de Entrada")
st.json(new_case)

st.subheader("📋 Casos Ordenados por Similaridade")
st.dataframe(df_sorted.drop(columns=["similarity"]))

# Previsão baseada nos vizinhos mais próximos
top_k = st.slider("Quantos vizinhos considerar para previsão?", 1, 20, 5)
most_common = df_sorted.head(top_k)["winner"].mode()[0]
st.success(f"🏆 Resultado previsto (baseado nos {top_k} mais similares): **{most_common.upper()}**")