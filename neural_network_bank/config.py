import torch

# Configuração do device
DEVICE = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

# Configurações da rede neural
MODEL_CONFIG = {
    'hidden1': 128,
    'hidden2': 64,
    'hidden3': 32,
    'dropout': 0.3
}

# Configurações de treinamento
TRAINING_CONFIG = {
    'batch_size': 64,
    'epochs': 1000,
    'learning_rate': 0.001,
    'validation_split': 0.2,
    'patience': 15
}

# Caminhos dos datasets
DATA_PATHS = {
    'train_dataset': 'datasets/bank.csv',
    'full_dataset': 'datasets/bank-full.csv',
    'best_model': 'best_model.pth'
}

# Configurações de visualização
PLOT_CONFIG = {
    'figsize': (15, 5),
    'dpi': 300,
    'confusion_matrix_size': (8, 6)
}
