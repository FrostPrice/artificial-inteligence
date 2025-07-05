import torch
import numpy as np
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, classification_report, confusion_matrix
import matplotlib.pyplot as plt
import seaborn as sns


def evaluate_model(model, X_test, y_test, dataset_name="Test", device=None):
    """
    Avaliar o modelo e calcular métricas
    """
    if device is None:
        device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    
    model.eval()
    
    # Converter para tensors
    X_test_tensor = torch.FloatTensor(X_test).to(device)
    
    # Fazer predições
    with torch.no_grad():
        outputs = model(X_test_tensor)
        predictions = (outputs > 0.5).cpu().numpy().flatten()
    
    # Calcular métricas
    accuracy = accuracy_score(y_test, predictions)
    precision = precision_score(y_test, predictions)
    recall = recall_score(y_test, predictions)
    f1 = f1_score(y_test, predictions)
    
    print(f"\n=== Métricas para {dataset_name} ===")
    print(f"Acurácia:  {accuracy:.4f}")
    print(f"Precisão:  {precision:.4f}")
    print(f"Revocação: {recall:.4f}")
    print(f"F1-Score:  {f1:.4f}")
    
    # Relatório detalhado
    print(f"\nRelatório de Classificação - {dataset_name}:")
    print(classification_report(y_test, predictions, target_names=['Não', 'Sim']))
    
    # Matriz de confusão
    cm = confusion_matrix(y_test, predictions)
    plt.figure(figsize=(8, 6))
    sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', 
                xticklabels=['Não', 'Sim'], yticklabels=['Não', 'Sim'])
    plt.title(f'Matriz de Confusão - {dataset_name}')
    plt.ylabel('Real')
    plt.xlabel('Predito')
    plt.tight_layout()
    plt.savefig(f'confusion_matrix_{dataset_name.lower()}.png', dpi=300, bbox_inches='tight')
    # plt.show()
    
    return accuracy, precision, recall, f1, predictions


def print_business_analysis(y_full, predictions_full):
    """
    Realizar análise de negócio baseada nas predições
    """
    print("\n" + "="*80)
    print("ANÁLISE PARA OTIMIZAÇÃO DE CAMPANHA DE MARKETING")
    print("="*80)
    
    total_customers = len(y_full)
    true_positives = np.sum((predictions_full == 1) & (y_full == 1))
    false_positives = np.sum((predictions_full == 1) & (y_full == 0))
    customers_targeted = np.sum(predictions_full == 1)
    
    print(f"Total de clientes no dataset completo: {total_customers:,}")
    print(f"Clientes que o modelo recomenda contatar: {customers_targeted:,}")
    print(f"Clientes que realmente fariam aplicação: {true_positives:,}")
    print(f"Clientes contatados desnecessariamente: {false_positives:,}")
    
    if customers_targeted > 0:
        success_rate = true_positives / customers_targeted
        print(f"Taxa de sucesso da campanha otimizada: {success_rate:.2%}")
        
        # Comparar com campanha aleatória
        random_success_rate = np.sum(y_full) / len(y_full)
        improvement = success_rate / random_success_rate
        print(f"Taxa de sucesso de campanha aleatória: {random_success_rate:.2%}")
        print(f"Melhoria com o modelo: {improvement:.2f}x")
        
        # Economia estimada
        cost_per_contact = 10  # euros por contato (estimativa)
        cost_without_model = total_customers * cost_per_contact
        cost_with_model = customers_targeted * cost_per_contact
        savings = cost_without_model - cost_with_model
        
        print(f"\nEstimativa de economia (assumindo €{cost_per_contact} por contato):")
        print(f"Custo sem modelo: €{cost_without_model:,}")
        print(f"Custo com modelo: €{cost_with_model:,}")
        print(f"Economia estimada: €{savings:,} ({(savings/cost_without_model)*100:.1f}%)")


def print_final_summary(acc_train, prec_train, rec_train, f1_train, 
                       acc_full, prec_full, rec_full, f1_full):
    """
    Imprimir resumo final das métricas
    """
    print("\n" + "="*80)
    print("RESUMO FINAL DAS MÉTRICAS")
    print("="*80)
    print(f"{'Métrica':<12} {'Treino':<10} {'Completo':<10} {'Diferença':<12}")
    print("-" * 50)
    print(f"{'Acurácia':<12} {acc_train:<10.4f} {acc_full:<10.4f} {abs(acc_train-acc_full):<12.4f}")
    print(f"{'Precisão':<12} {prec_train:<10.4f} {prec_full:<10.4f} {abs(prec_train-prec_full):<12.4f}")
    print(f"{'Revocação':<12} {rec_train:<10.4f} {rec_full:<10.4f} {abs(rec_train-rec_full):<12.4f}")
    print(f"{'F1-Score':<12} {f1_train:<10.4f} {f1_full:<10.4f} {abs(f1_train-f1_full):<12.4f}")
