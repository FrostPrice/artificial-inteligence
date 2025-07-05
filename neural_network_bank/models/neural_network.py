import torch
import torch.nn as nn


class BankNeuralNetwork(nn.Module):
    """
    Rede Neural para predição de aplicação bancária
    """
    def __init__(self, input_size, hidden1=128, hidden2=64, hidden3=32, dropout=0.3):
        super(BankNeuralNetwork, self).__init__()
        
        self.network = nn.Sequential(
            nn.Linear(input_size, hidden1),
            nn.ReLU(),
            nn.BatchNorm1d(hidden1),
            nn.Dropout(dropout),
            
            nn.Linear(hidden1, hidden2),
            nn.ReLU(),
            nn.BatchNorm1d(hidden2),
            nn.Dropout(dropout),
            
            nn.Linear(hidden2, hidden3),
            nn.ReLU(),
            nn.BatchNorm1d(hidden3),
            nn.Dropout(dropout),
            
            nn.Linear(hidden3, 1),
            nn.Sigmoid()
        )
    
    def forward(self, x):
        return self.network(x)
