# CliqCompra

## Sumário
- [Sobre o Projeto](#sobre-o-projeto)
- [Inspiração e Objetivo Final](#inspiração-e-objetivo-final)
- [Funcionalidades Atuais](#funcionalidades-atuais)
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
