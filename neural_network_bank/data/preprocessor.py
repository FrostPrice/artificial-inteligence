import pandas as pd
import numpy as np
import torch
from torch.utils.data import DataLoader, TensorDataset
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.model_selection import train_test_split


def preprocess_data(df):
    """
    Pré-processamento dos dados
    """
    # Fazer uma cópia para não modificar o dataset original
    data = df.copy()
    
    # Encoders para variáveis categóricas
    label_encoders = {}
    categorical_columns = ['job', 'marital', 'education', 'default', 'housing', 'loan', 
                          'contact', 'month', 'poutcome']
    
    for col in categorical_columns:
        le = LabelEncoder()
        data[col] = le.fit_transform(data[col])
        label_encoders[col] = le
    
    # Separar features e target
    X = data.drop('y', axis=1)
    y = LabelEncoder().fit_transform(data['y'])  # 'no'=0, 'yes'=1
    
    return X, y, label_encoders


def load_and_preprocess_data():
    """
    Carregar e pré-processar os datasets
    """
    print("Carregando datasets...")
    
    # Carregar datasets
    train_df = pd.read_csv('datasets/bank.csv', sep=';')
    full_df = pd.read_csv('datasets/bank-full.csv', sep=';')
    
    print(f"Dataset de treino: {train_df.shape}")
    print(f"Dataset completo: {full_df.shape}")
    
    # Verificar distribuição das classes
    print("\nDistribuição das classes no dataset de treino:")
    print(train_df['y'].value_counts())
    print(f"Proporção de 'yes': {train_df['y'].value_counts()['yes'] / len(train_df):.3f}")
    
    print("\nDistribuição das classes no dataset completo:")
    print(full_df['y'].value_counts())
    print(f"Proporção de 'yes': {full_df['y'].value_counts()['yes'] / len(full_df):.3f}")
    
    # Pré-processar dados
    X_train, y_train, _ = preprocess_data(train_df)
    X_full, y_full, _ = preprocess_data(full_df)
    
    # Normalizar features
    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_full_scaled = scaler.transform(X_full)
    
    return X_train_scaled, y_train, X_full_scaled, y_full, scaler


def create_data_loaders(X_train, y_train, batch_size=64, validation_split=0.2):
    """
    Criar DataLoaders para treino e validação
    """
    # Dividir treino em treino/validação
    X_tr, X_val, y_tr, y_val = train_test_split(
        X_train, y_train, test_size=validation_split, random_state=42, stratify=y_train
    )
    
    # Converter para tensors
    X_tr_tensor = torch.FloatTensor(X_tr)
    y_tr_tensor = torch.FloatTensor(y_tr).reshape(-1, 1)
    X_val_tensor = torch.FloatTensor(X_val)
    y_val_tensor = torch.FloatTensor(y_val).reshape(-1, 1)
    
    # Criar datasets
    train_dataset = TensorDataset(X_tr_tensor, y_tr_tensor)
    val_dataset = TensorDataset(X_val_tensor, y_val_tensor)
    
    # Criar data loaders
    train_loader = DataLoader(train_dataset, batch_size=batch_size, shuffle=True)
    val_loader = DataLoader(val_dataset, batch_size=batch_size, shuffle=False)
    
    return train_loader, val_loader
