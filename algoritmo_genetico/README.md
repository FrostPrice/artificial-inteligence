# 📈 Genetic Algorithm for Stock Allocation Optimization (B3 - Brazil)

This project implements a **genetic algorithm** to find the best allocation strategy for stocks listed on the B3 (Brazilian Stock Exchange), aiming to **maximize financial return** from an initial investment through simulated buy/sell cycles.

---

## 🧠 Project Overview

- Input: CSV file with daily closing stock prices
- Goal: Allocate 10 investment buckets per cycle (buy/sell) to maximize final value
- Technique: Genetic algorithm with tournament selection, crossover, and mutation
- Output:
  - Best DNA (allocation of stocks per cycle)
  - Final amount achieved
  - Graphs showing performance across generations and cycles

---

## 📊 CSV File Format Example

```
Date;StockCode;ClosePrice
2025-04-01 00:00:00;B3SA3;12,26
2025-04-01 00:00:00;PETR4;36,12
...
```

- Only 5-character codes are considered valid (i.e., real stocks).
- Uses **Brazilian number format** with `,` as the decimal separator.
- Ignores invalid or incomplete lines automatically.

---

## ⚙️ Algorithm Mechanics

### Cycles

Each cycle simulates:

- Buy: on day N
- Sell: on day N+1
- The result is redistributed into 10 buckets for the next cycle

### DNA Structure

- Each DNA consists of `NUM_CYCLES * 10` stock codes.
- Each group of 10 represents the allocation for one cycle.

### Genetic Strategy

- Initial random population
- Tournament selection (k = 3)
- Crossover rate: 70%
- Mutation rate: 10%
- 100 generations by default (configurable)

---

## 📦 Features

- ✅ DNA evaluation with capital updated each cycle
- ✅ Historical value tracking per cycle
- ✅ Performance graph by generation (`grafico_evolucao.png`)
- ✅ Cycle-by-cycle capital evolution (`grafico_por_ciclo.png`)
- ✅ Console prints for gain/loss per cycle

---

## 📊 Sample Output

```txt
Generation 100: Best final value = R$ 12248.69

Best allocation found:
Cycle 1: ['B3SA3', 'PETR4', ..., 'VALE3']
...

Cycle variations:
Cycle 1: gain of 5.43% (R$ 1054.28)
Cycle 2: loss of -2.17% (R$ 1031.39)
...

Graph saved as 'grafico_evolucao.png'
Graph saved as 'grafico_por_ciclo.png'
```

---

## 🧪 Requirements

- Python 3.8+
- Libraries:
  - `matplotlib`
  - Standard: `csv`, `random`, `collections`

---

## 🚀 How to Run

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

Make sure the file `cotacoes_b3_202_05.csv` is in the same folder and correctly formatted.

---

## 📁 Expected Structure

```txt
📦genetic_stock_allocator
 ┣ 📄main.py
 ┣ 📄cotacoes_b3_202_05.csv
 ┣ 📄grafico_evolucao.png
 ┣ 📄grafico_por_ciclo.png
 ┗ 📄README.txt
```

---

## 📈 Generated Results

- `grafico_evolucao.png`: Best individual's value across generations
- `grafico_por_ciclo.png`: Final capital per cycle for the best DNA

---

## 📜 License

MIT License — Free for academic and educational use.

---

## 👨‍🏫 Academic Note

This project was developed as part of an academic assignment for the **Artificial Intelligence** course (UNIVALI, 2025), applying evolutionary algorithms to financial optimization problems.
