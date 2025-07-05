# Sistema de Predição de Aplicação Bancária

Este projeto implementa uma rede neural para predizer a probabilidade de clientes fazerem aplicações bancárias, otimizando campanhas de marketing.

## Estrutura do Projeto

```text
neural_network_bank/
├── main.py                    # Arquivo principal de execução
├── config.py                 # Configurações centralizadas
├── requirements.txt          # Dependências do projeto
├── best_model.pth           # Melhor modelo treinado
├── datasets/                # Datasets de entrada
│   ├── bank.csv
│   ├── bank-full.csv
│   └── bank-names.txt
├── models/                  # Modelos de machine learning
│   ├── __init__.py
│   └── neural_network.py    # Definição da rede neural
├── data/                    # Processamento de dados
│   ├── __init__.py
│   └── preprocessor.py      # Pré-processamento e carregamento
├── training/                # Treinamento de modelos
│   ├── __init__.py
│   └── trainer.py           # Lógica de treinamento
├── evaluation/              # Avaliação e métricas
│   ├── __init__.py
│   └── evaluator.py         # Métricas e análise de negócio
└── utils/                   # Utilitários
    ├── __init__.py
    └── visualizer.py        # Visualizações e gráficos
```

## Módulos

### 1. `models/neural_network.py`

- Classe `BankNeuralNetwork`: Implementação da rede neural
- Arquitetura: 4 camadas densas com BatchNorm, Dropout e ReLU

### 2. `data/preprocessor.py`

- `preprocess_data()`: Pré-processamento dos dados
- `load_and_preprocess_data()`: Carregamento e preparação dos datasets
- `create_data_loaders()`: Criação dos DataLoaders do PyTorch

### 3. `training/trainer.py`

- `train_model()`: Função principal de treinamento
- Implementa early stopping, learning rate scheduling e checkpoints

### 4. `evaluation/evaluator.py`

- `evaluate_model()`: Avaliação do modelo com métricas
- `print_business_analysis()`: Análise de negócio
- `print_final_summary()`: Resumo das métricas

### 5. `utils/visualizer.py`

- `plot_training_history()`: Gráficos do histórico de treinamento

### 6. `config.py`

- Configurações centralizadas do projeto
- Parâmetros da rede, treinamento e visualização

## Como Executar

```bash
# Instalar dependências
pip install -r requirements.txt

# Executar o sistema
python main.py
```
