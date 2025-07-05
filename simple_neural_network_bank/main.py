import pandas as pd
import numpy as np
import torch
import torch.nn as nn
from torch.utils.data import Dataset, DataLoader
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, confusion_matrix

# ===============================
# 1. Função para carregar e preparar dados
# ===============================
def load_and_prepare(csv_path, scaler=None, fit_scaler=False):
    df = pd.read_csv(
        csv_path,
        sep=';',
        quotechar='"',
        true_values=['yes'],
        false_values=['no']
    )

    df['y'] = df['y'].astype(int)
    categorical_cols = df.select_dtypes(include=['object']).columns
    df = pd.get_dummies(df, columns=categorical_cols)

    # Garante que o DataFrame tenha todas as colunas necessárias
    if fit_scaler:
        X = df.drop('y', axis=1)
        scaler = StandardScaler().fit(X)
    X = df.drop('y', axis=1)
    X_scaled = scaler.transform(X)

    y = df['y'].values
    return X_scaled, y, scaler, df.columns.drop('y')

# ===============================
# 2. Dataset PyTorch
# ===============================
class BankDataset(Dataset):
    def __init__(self, X, y):
        self.X = torch.tensor(X, dtype=torch.float32)
        self.y = torch.tensor(y, dtype=torch.float32).view(-1, 1)

    def __len__(self):
        return len(self.y)

    def __getitem__(self, idx):
        return self.X[idx], self.y[idx]

# ===============================
# 3. Modelo
# ===============================
class BankModel(nn.Module):
    def __init__(self, input_size):
        super(BankModel, self).__init__()
        self.net = nn.Sequential(
            nn.Linear(input_size, 32),
            nn.ReLU(),
            nn.Linear(32, 16),
            nn.ReLU(),
            nn.Linear(16, 1),
            nn.Sigmoid()
        )

    def forward(self, x):
        return self.net(x)

# ===============================
# 4. Carregar dados de treino (bank.csv)
# ===============================
print("Carregando dados de treino...")
X_train_raw, y_train, scaler, feature_cols = load_and_prepare(
    'dataset_bank/bank.csv', fit_scaler=True
)
X_train, X_val, y_train, y_val = train_test_split(X_train_raw, y_train, test_size=0.2)

train_dataset = BankDataset(X_train, y_train)
val_dataset = BankDataset(X_val, y_val)

train_loader = DataLoader(train_dataset, batch_size=32, shuffle=True)
val_loader = DataLoader(val_dataset, batch_size=32)

model = BankModel(input_size=X_train.shape[1])
criterion = nn.BCELoss()
optimizer = torch.optim.Adam(model.parameters(), lr=0.001)

# ===============================
# 5. Treinamento
# ===============================
print("Treinando modelo...")
for epoch in range(1000):
    model.train()
    total_loss = 0
    for X_batch, y_batch in train_loader:
        outputs = model(X_batch)
        loss = criterion(outputs, y_batch)
        optimizer.zero_grad()
        loss.backward()
        optimizer.step()
        total_loss += loss.item()
    print(f"Epoch {epoch+1}, Loss: {total_loss:.4f}")

# ===============================
# 6. Avaliação em val_loader
# ===============================
def evaluate(model, loader, name=""):
    model.eval()
    y_true = []
    y_pred = []

    with torch.no_grad():
        for X_batch, y_batch in loader:
            outputs = model(X_batch)
            predicted = (outputs > 0.5).float()
            y_true.extend(y_batch.view(-1).tolist())
            y_pred.extend(predicted.view(-1).tolist())

    y_true = np.array(y_true)
    y_pred = np.array(y_pred)

    acc = accuracy_score(y_true, y_pred)
    prec = precision_score(y_true, y_pred, zero_division=0)
    rec = recall_score(y_true, y_pred, zero_division=0)
    f1 = f1_score(y_true, y_pred, zero_division=0)
    cm = confusion_matrix(y_true, y_pred)

    print(f"\n📊 Métricas de avaliação – {name}")
    print(f"  Acurácia : {acc:.4f}")
    print(f"  Precisão : {prec:.4f}")
    print(f"  Revocação: {rec:.4f}")
    print(f"  F1 Score : {f1:.4f}")
    print("\n🧮 Matriz de Confusão:")
    print("  [[TN FP]")
    print("   [FN TP]]")
    print(cm)
    print()

    return acc, prec, rec, f1



evaluate(model, val_loader, name="validação interna (bank.csv)")

# ===============================
# 7. Validação com bank-full.csv
# ===============================
print("Validando com bank-full.csv...")
X_full, y_full, _, _ = load_and_prepare('dataset_bank/bank-full.csv', scaler=scaler)
full_dataset = BankDataset(X_full, y_full)
full_loader = DataLoader(full_dataset, batch_size=32)

evaluate(model, full_loader, name="validação externa (bank-full.csv)")
