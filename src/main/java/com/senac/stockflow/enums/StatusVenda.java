package com.senac.stockflow.enums;

public enum StatusVenda {
    SUCESSO,
    ERRO_PAGAMENTO, // Para simular falhas de cartão
    ERRO_ESTOQUE    // Para quando tentar comprar e já acabou
}
