import csv
import random
import matplotlib.pyplot as plt
from collections import defaultdict

# CONFIGURAÇÕES
NUM_POTES = 10
VALOR_INICIAL = 1000.0
POP_SIZE = 30
NUM_GERACOES = 5000
TAXA_MUTACAO = 0.1
TAXA_CROSSOVER = 0.7
TOURNAMENT_SIZE = 3

# Lê os dados do CSV
def ler_dados_csv(caminho):
    cotacoes = defaultdict(dict)
    dias_ordenados = set()
    with open(caminho, newline='', encoding='utf-8') as csvfile:
        leitor = csv.reader(csvfile, delimiter=';')
        next(leitor)
        for linha in leitor:
            if len(linha) < 3:
                continue
            data = linha[0].split()[0]
            codigo = linha[1].strip()
            try:
                preco = float(linha[2].replace(',', '.'))
            except ValueError:
                continue
            if len(codigo) == 5:
                cotacoes[data][codigo] = preco
                dias_ordenados.add(data)
    return sorted(dias_ordenados), cotacoes

def gerar_dna(codigos, num_ciclos):
    return [random.choice(codigos) for _ in range(num_ciclos * NUM_POTES)]

def avaliar_dna(dna, dias, cotacoes, num_ciclos, verbose=False):
    montante = VALOR_INICIAL
    historico = [montante]
    for ciclo in range(num_ciclos):
        dia_compra = dias[ciclo * 2]
        dia_venda = dias[ciclo * 2 + 1]
        novo_montante = 0.0
        valor_pote = montante / NUM_POTES
        for pote in range(NUM_POTES):
            codigo = dna[ciclo * NUM_POTES + pote]
            preco_compra = cotacoes[dia_compra].get(codigo)
            preco_venda = cotacoes[dia_venda].get(codigo)
            if preco_compra and preco_venda:
                quantidade = valor_pote / preco_compra
                novo_montante += quantidade * preco_venda
            else:
                novo_montante += valor_pote
        if verbose:
            lucro = novo_montante - montante
            print(f"Ciclo {ciclo + 1}: {dia_compra} → {dia_venda} | Valor: R$ {novo_montante:.2f} | {'Lucro' if lucro >= 0 else 'Prejuízo'}: R$ {lucro:.2f}")
        montante = novo_montante
        historico.append(montante)
    return montante, historico

# Seleção por torneio
def selecao_torneio(populacao, fitness):
    selecionados = random.sample(list(zip(populacao, fitness)), TOURNAMENT_SIZE)
    return max(selecionados, key=lambda x: x[1])[0]

# Crossover
def crossover(dna1, dna2):
    if random.random() > TAXA_CROSSOVER:
        return dna1[:], dna2[:]
    ponto = random.randint(1, len(dna1) - 2)
    return dna1[:ponto] + dna2[ponto:], dna2[:ponto] + dna1[ponto:]

# Mutação
def mutar(dna, codigos):
    for i in range(len(dna)):
        if random.random() < TAXA_MUTACAO:
            dna[i] = random.choice(codigos)

# Algoritmo Genético Principal
def algoritmo_genetico(caminho_csv):
    dias, cotacoes = ler_dados_csv(caminho_csv)
    NUM_DIAS_VALIDOS = len(dias)
    NUM_CICLOS = NUM_DIAS_VALIDOS // 2

    codigos_validos = list({codigo for dia in dias for codigo in cotacoes[dia].keys()})
    if len(codigos_validos) == 0:
        raise ValueError("Nenhum código de ação válido foi encontrado.")

    populacao = [gerar_dna(codigos_validos, NUM_CICLOS) for _ in range(POP_SIZE)]
    historico_melhor = []

    for geracao in range(NUM_GERACOES):
        fitness = []
        for individuo in populacao:
            valor_final, _ = avaliar_dna(individuo, dias, cotacoes, NUM_CICLOS)
            fitness.append(valor_final)

        nova_populacao = []
        while len(nova_populacao) < POP_SIZE:
            pai1 = selecao_torneio(populacao, fitness)
            pai2 = selecao_torneio(populacao, fitness)
            filho1, filho2 = crossover(pai1, pai2)
            mutar(filho1, codigos_validos)
            mutar(filho2, codigos_validos)
            nova_populacao.extend([filho1, filho2])
        populacao = nova_populacao[:POP_SIZE]

        melhor_fitness = max(fitness)
        historico_melhor.append(melhor_fitness)
        print(f"Geração {geracao + 1}: Melhor valor final = R$ {melhor_fitness:.2f}")

    melhor_individuo = max(zip(populacao, fitness), key=lambda x: x[1])
    return melhor_individuo[0], melhor_individuo[1], NUM_CICLOS, dias, cotacoes, historico_melhor

melhor_dna, melhor_valor, NUM_CICLOS, dias, cotacoes, historico_melhor = algoritmo_genetico("./cotacoes_b3_202_05.csv")

print("\nMelhor alocação encontrada:")
_, historico_por_ciclo = avaliar_dna(melhor_dna, dias, cotacoes, NUM_CICLOS, verbose=True)
for ciclo in range(NUM_CICLOS):
    print(f"Ciclo {ciclo + 1}: {melhor_dna[ciclo * NUM_POTES:(ciclo + 1) * NUM_POTES]}")
print(f"Valor final: R$ {melhor_valor:.2f}")

# Plot da evolução do melhor valor por geração
plt.plot(range(1, len(historico_melhor) + 1), historico_melhor)
plt.xlabel("Geração")
plt.ylabel("Melhor valor (R$)")
plt.title("Evolução do melhor valor por geração")
plt.grid(True)
plt.tight_layout()
plt.savefig("grafico_evolucao.png")
print("Gráfico salvo como 'grafico_evolucao.png'")

print("\nVariação por ciclo:")
for i in range(1, len(historico_por_ciclo)):
    anterior = historico_por_ciclo[i - 1]
    atual = historico_por_ciclo[i]
    variacao = ((atual - anterior) / anterior) * 100
    status = "lucro" if variacao > 0 else "prejuízo"
    print(f"Ciclo {i}: {status} de {variacao:.2f}% (R$ {atual:.2f})")

plt.figure()
plt.plot(range(len(historico_por_ciclo)), historico_por_ciclo, marker='o')
plt.xlabel("Ciclo")
plt.ylabel("Montante (R$)")
plt.title("Evolução do Montante por Ciclo")
plt.grid(True)
plt.tight_layout()
plt.savefig("grafico_por_ciclo.png")
print("Gráfico salvo como 'grafico_por_ciclo.png'")
