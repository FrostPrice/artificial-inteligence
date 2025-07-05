import matplotlib.pyplot as plt


def plot_training_history(train_losses, val_losses, val_accuracies):
    """
    Plotar histórico de treinamento
    """
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(15, 5))
    
    # Loss
    ax1.plot(train_losses, label='Treino', color='blue')
    ax1.plot(val_losses, label='Validação', color='red')
    ax1.set_title('Evolução da Loss')
    ax1.set_xlabel('Época')
    ax1.set_ylabel('Loss')
    ax1.legend()
    ax1.grid(True)
    
    # Acurácia
    ax2.plot(val_accuracies, label='Validação', color='green')
    ax2.set_title('Evolução da Acurácia')
    ax2.set_xlabel('Época')
    ax2.set_ylabel('Acurácia')
    ax2.legend()
    ax2.grid(True)
    
    plt.tight_layout()
    plt.savefig('training_history.png', dpi=300, bbox_inches='tight')
    # plt.show()
