# CliqCompra

## Sumário
- [Sobre o Projeto](#sobre-o-projeto)
- [Inspiração e Objetivo Final](#inspiração-e-objetivo-final)
- [Funcionalidades Atuais](#funcionalidades-atuais)
- [Visão de Futuro (Rumo a ser como OLX)](#visão-de-futuro-rumo-a-ser-como-olx)
- [Modelo de Dados (UML)](#modelo-de-dados-uml)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Como Executar](#como-executar)

## Sobre o Projeto
CliqCompra é uma plataforma de e-commerce em desenvolvimento, construída com Java e Spring Boot. O projeto visa criar um ambiente online para a compra e venda de produtos, servindo como um intermediário entre usuários. A ideia é facilitar as transações comerciais de forma simples e eficiente.

## Inspiração e Objetivo Final
O projeto é **inspirado em plataformas de sucesso de intermediação de vendas, como a OLX**. O objetivo final é evoluir o CliqCompra para um marketplace completo e robusto, onde usuários (tanto consumidores quanto vendedores – Pessoas Físicas ou Jurídicas) possam anunciar, descobrir e negociar uma ampla variedade de produtos de forma intuitiva e segura. Queremos facilitar a conexão direta entre quem quer vender e quem quer comprar, promovendo um ecossistema de comércio dinâmico e acessível.

## Funcionalidades Atuais
Atualmente, o projeto implementa as seguintes funcionalidades:
* **Gerenciamento de Usuários:**
    * Cadastro de novos usuários (o fluxo principal de cadastro atualmente foca no perfil de `Cliente`).
    * Login e Logout de usuários com persistência de sessão.
    * Hashing de senhas para segurança utilizando BCrypt.
* **Gerenciamento de Produtos:**
    * Listagem de todos os produtos disponíveis para compra.
    * Permite que usuários logados adicionem novos produtos ao sistema, associando o produto ao usuário que o cadastrou.
    * Upload de fotos para os produtos, que são armazenadas localmente e exibidas nas listagens.
    * Visualização dos produtos anunciados pelo próprio usuário na seção "Meus Produtos".
* **Carrinho de Compras:**
    * Adição de produtos ao carrinho de compras.
    * Visualização detalhada do carrinho.
    * Atualização da quantidade de itens no carrinho.
    * Remoção de itens do carrinho.
    * O carrinho de compras é mantido por sessão de usuário.
* **Processo de Compra (Checkout):**
    * Seleção de endereço de entrega a partir dos endereços previamente cadastrados pelo `Cliente`.
    * Gerenciamento de múltiplos endereços pelo `Cliente` (Adicionar e Listar).
    * Página de confirmação dos detalhes do pedido antes da finalização.
    * Finalização do pedido, com registro no sistema e limpeza do carrinho.
* **Histórico de Compras:**
    * Clientes podem visualizar o histórico de seus pedidos realizados.
    * Página de confirmação/detalhamento do pedido acessível após a compra ou pelo histórico.
* **API REST:**
    * Endpoints básicos foram criados para `Pedidos`, `Produtos` e `Usuários`, permitindo interações via HTTP.

## Visão de Futuro (Rumo a ser como OLX)
Para alcançar o objetivo de ser uma plataforma de intermediação de vendas completa e versátil como a OLX, os próximos passos e funcionalidades planejadas incluem:
* **Perfis Detalhados para Vendedores:** Implementar funcionalidades específicas para `Vendedores`, como um painel de controle para gerenciar anúncios, vendas, visualizar estatísticas e gerenciar o perfil público.
* **Comunicação Direta (Chat):** Desenvolver um sistema de mensagens ou chat em tempo real para que compradores e vendedores possam negociar e tirar dúvidas diretamente na plataforma.
* **Busca Avançada e Filtros:** Criar mecanismos de busca mais sofisticados, com filtros por categoria, faixa de preço, localização do vendedor, condição do produto (novo/usado), entre outros.
* **Sistema de Avaliações e Reputação:** Permitir que usuários avaliem produtos, vendedores e compradores, construindo um sistema de reputação que aumente a confiança nas transações.
* **Integração com Pagamentos Seguros:** Integrar com gateways de pagamento online para processar transações financeiras de forma segura diretamente na plataforma, oferecendo opções como cartão de crédito, boleto, Pix, etc.
* **Categorização Flexível e Detalhada:** Melhorar e expandir o sistema de categorização de produtos para abranger uma maior variedade de itens e facilitar a navegação.
* **Painel Administrativo:** Uma interface para administradores da plataforma gerenciarem usuários, anúncios, categorias, resolver disputas e monitorar a atividade do site.
* **Notificações em Tempo Real:** Implementar um sistema de notificações para alertar usuários sobre novas mensagens, ofertas, status de pedidos, novos anúncios de interesse, etc.
* **Geolocalização para Anúncios:** Permitir que anúncios sejam associados a uma localização e que usuários possam buscar produtos próximos.
* **Múltiplos Tipos de Anúncio:** Considerar a possibilidade de diferentes tipos de listagens, como serviços, aluguéis, além de produtos físicos.

## Modelo de Dados (UML)
A estrutura do modelo de dados do sistema está representada no diagrama UML abaixo, que foi concebido para suportar as funcionalidades atuais e futuras da plataforma:

```plantuml
@startuml
skinparam classAttributeIconSize 0

abstract class Usuario {
  + id: Integer
  + nome: String
  + email: String
  # senhaHash: String
  + cpfCnpj: String
  + telefones: List<String>
  + fazerLogin(email, senha): boolean
  + editarPerfil(): void
}

class Cliente extends Usuario {
  + enderecos: List<Endereco>
  + cadastrar(): void
  + realizarPedido(pedido: Pedido): void
  + selecionarEnderecoEntrega(endereco: Endereco): void
  + visualizarPedidos(): List<Pedido>
  + adicionarProdutoAoCarrinho(produto: Produto, quantidade: int): void
}

class Vendedor extends Usuario {
  + cadastrar(): void
  + cadastrarProduto(produto: Produto): void
  + aplicarDescontoItem(item: ItemPedido, valorDesconto: double): void
  + visualizarPedidos(): List<Pedido>
}

class Produto {
  + id: Integer
  + nome: String
  + preco: double
  + descricao: String
  + adicionarCategoria(categoria: Categoria): void
  + removerCategoria(categoria: Categoria): void
}

class Categoria {
  + id: Integer
  + nome: String
}

class Pedido {
  + id: Integer
  + dataRealizacao: Timestamp
  + status: StatusPedido
  + valorTotal: double
  + adicionarItem(produto: Produto, quantidade: int): void
  + removerItem(produto: Produto): void
  + calcularTotal(): double
  + finalizarPedido(): void
  + atualizarStatus(novoStatus: StatusPedido): void
  + selecionarFormaPagamento(pagamento: Pagamento): void
}

enum StatusPedido {
  PENDENTE
  PROCESSANDO
  ENVIADO
  ENTREGUE
  CANCELADO
}

class ItemPedido {
  + id: Integer
  + quantidade: int
  + precoUnitarioMomentoCompra: double
  + subtotal: double
  + descontoAplicado: double
}

class Endereco {
  + id: Integer
  + rua: String
  + numero: String
  + complemento: String
  + bairro: String
  + cidade: String
  + estado: String
  + cep: String
}

abstract class Pagamento {
  + id: Integer
  + valor: double
  + dataPagamento: Timestamp
  + status: StatusPagamento
  + processarPagamento(): boolean
}

enum StatusPagamento {
  PENDENTE
  APROVADO
  RECUSADO
  REEMBOLSADO
}

class PagamentoBoleto extends Pagamento {
  + codigoBarras: String
  + dataVencimento: Date
  + emitirBoleto(): void
}

class PagamentoCartao extends Pagamento {
  + numeroCartaoToken: String
  + nomeTitular: String
  + dataValidade: String
  + numeroParcelas: int
  + registrarPagamentoCartao(): void
}

' Relacionamentos
Cliente "1" -- "*" Endereco : possui >
Cliente "1" -- "0..*" Pedido : faz >
Vendedor "1" -- "0..*" Produto : cadastra >

Pedido "1" -- "1" Cliente : feito por <
Pedido "1" -- "1" Endereco : entregue em >
Pedido "1" o-- "1..*" ItemPedido : contém (Composição)
ItemPedido "1" -- "1" Produto : refere-se a >
Produto "" -- "" Categoria : pertence a (Associação Muitos-para-Muitos)
' Alternativa para Produto-Categoria (Produto tem uma lista de Categorias)
' Produto "1" -- "0..*" Categoria : possui >

Pedido "1" -- "1" Pagamento : pago com >
@enduml
