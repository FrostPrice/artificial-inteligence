import csv
from collections import defaultdict

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

