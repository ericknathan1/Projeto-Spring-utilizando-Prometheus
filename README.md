# Stockflow - Sistema de Monitoramento e Venda de Ingressos

## Resumo do Projeto
O **Stockflow** é uma aplicação backend desenvolvida em Java com Spring Boot, projetada para simular um ecossistema de alta demanda de venda de ingressos. O objetivo principal deste projeto não é apenas o gerenciamento de transações, mas sim a implementação de práticas robustas de **observabilidade**.

A aplicação expõe métricas customizadas via **Spring Boot Actuator** e **Micrometer**, que são coletadas pelo **Prometheus** e visualizadas no **Grafana**. O sistema inclui funcionalidades para simulação de carga, geração de erros aleatórios (falhas de pagamento) e controle de concorrência de estoque, permitindo a análise de resiliência e comportamento da API sob estresse.

## Tecnologias Utilizadas

O projeto utiliza uma stack moderna focada em performance e monitoramento:

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3 (Web, Data JPA, Actuator)
* **Banco de Dados:** H2 Database (In-memory para facilitar testes e portabilidade)
* **Monitoramento & Métricas:**
    * Micrometer (Instrumentação de código)
    * Prometheus (Coleta de séries temporais)
    * Grafana (Visualização e Dashboards)
* **Infraestrutura:** Docker e Docker Compose
* **Frontend de Teste:** HTML5, TailwindCSS e JavaScript (Painel para geração de tráfego automatizado)

## Funcionalidades Principais

1.  **Gestão de Vendas:** Processamento de compra de tickets com validação de estoque e tipos de ingresso (PISTA, VIP, CAMAROTE).
2.  **Métricas Customizadas:**
    * `ticket.vendas.sucesso`: Contador de vendas efetuadas por tipo.
    * `ticket.vendas.erro`: Contador de falhas (pagamento recusado ou estoque esgotado).
    * `ticket.estoque.total`: Gauge que monitora o nível do estoque em tempo real.
    * `ticket.compra.latencia`: Timer para medir o tempo de resposta das requisições.
3.  **Simulação de Falhas:** Introdução proposital de latência (sleep aleatório) e erros de pagamento (10% de chance) para enriquecer a visualização nos gráficos.

## Como Executar

### Pré-requisitos
* Java JDK 21 instalado.
* Docker e Docker Compose instalados.

### Passo 1: Inicializar a Infraestrutura de Monitoramento
Utilize o Docker Compose para subir os containers do Prometheus e Grafana.

```bash
docker-compose up -d
