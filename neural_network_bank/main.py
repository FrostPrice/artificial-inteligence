#!/usr/bin/env python3
"""
Sistema de Predição de Aplicação Bancária
Rede Neural para otimização de campanhas de marketing
"""

import torch
from models.neural_network import BankNeuralNetwork
from data.preprocessor import load_and_preprocess_data, create_data_loaders
from training.trainer import train_model
from evaluation.evaluator import evaluate_model, print_business_analysis, print_final_summary
from utils.visualizer import plot_training_history
from config import DEVICE, MODEL_CONFIG, TRAINING_CONFIG

print(f"Usando device: {DEVICE}")


def main():
    """
    Função principal
    """
    print("=== REDE NEURAL PARA PREDIÇÃO DE APLICAÇÃO BANCÁRIA ===\n")
    
    # Carregar e pré-processar dados
    X_train, y_train, X_full, y_full, scaler = load_and_preprocess_data()
    
    # Criar data loaders
    train_loader, val_loader = create_data_loaders(
        X_train, y_train, 
        batch_size=TRAINING_CONFIG['batch_size'],
        validation_split=TRAINING_CONFIG['validation_split']
    )
    
    # Criar modelo
    input_size = X_train.shape[1]
    model = BankNeuralNetwork(
        input_size=input_size,
        hidden1=MODEL_CONFIG['hidden1'],
        hidden2=MODEL_CONFIG['hidden2'],
        hidden3=MODEL_CONFIG['hidden3'],
        dropout=MODEL_CONFIG['dropout']
    ).to(DEVICE)
    
    print(f"\nArquitetura do modelo:")
    print(model)
    print(f"Número de parâmetros: {sum(p.numel() for p in model.parameters()):,}")
    
    # Treinar modelo
    train_losses, val_losses, val_accuracies = train_model(
        model=model,
        train_loader=train_loader,
        val_loader=val_loader,
        epochs=TRAINING_CONFIG['epochs'],
        learning_rate=TRAINING_CONFIG['learning_rate'],
        device=DEVICE
    )
    
    # Plotar histórico de treinamento
    plot_training_history(train_losses, val_losses, val_accuracies)
    
    # Avaliar no dataset de treino
    print("\n" + "="*60)
    print("AVALIAÇÃO NO DATASET DE TREINO")
    print("="*60)
    acc_train, prec_train, rec_train, f1_train, _ = evaluate_model(
        model, X_train, y_train, "Dataset de Treino", DEVICE
    )
    
    # Avaliar no dataset completo (validação final)
    print("\n" + "="*60)
    print("AVALIAÇÃO NO DATASET COMPLETO (VALIDAÇÃO FINAL)")
    print("="*60)
    acc_full, prec_full, rec_full, f1_full, predictions_full = evaluate_model(
        model, X_full, y_full, "Dataset Completo", DEVICE
    )
    
    # Resumo final das métricas
    print_final_summary(acc_train, prec_train, rec_train, f1_train, 
                       acc_full, prec_full, rec_full, f1_full)
    
    # Análise do negócio
    print_business_analysis(y_full, predictions_full)

if __name__ == "__main__":
    main()
