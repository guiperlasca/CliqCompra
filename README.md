# CliqCompra: Plataforma de E-commerce

## Sumário
- [Sobre o Projeto](#sobre-o-projeto)
- [Modelo de Dados (UML)](#modelo-de-dados-uml)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Autores](#autores)

---
## Sobre o Projeto
CliqCompra é uma plataforma de e-commerce em desenvolvimento, construída com Java e Spring Boot. Originalmente intitulado "Sistema Integrado de Mediação de Compras e Vendas para Usuários em Ambiente Virtual", o sistema visa criar um ambiente online para a compra e venda de produtos, atuando como intermediário entre usuários.

O objetivo é simular uma estrutura básica de e-commerce, permitindo a interação entre compradores e vendedores, a divulgação de produtos, a realização de pedidos e o gerenciamento de pagamentos.

Este é um projeto acadêmico desenvolvido com foco na aplicação prática de conceitos de desenvolvimento de software e na experimentação de funcionalidades de um sistema web real de forma acessível.

---
## Modelo de Dados (UML)
A estrutura do modelo de dados do sistema está representada no diagrama UML abaixo, concebido para suportar as funcionalidades da plataforma:

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
  + fotoUrl: String
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
Usuario <|-- Cliente
Usuario <|-- Vendedor

Cliente "1" -- "*" Endereco : possui >
Cliente "1" -- "0..*" Pedido : faz >
Vendedor "1" -- "0..*" Produto : cadastra <
' Ou melhor: Usuario "1" -- "0..*" Produto : cadastra > (Produto tem um ManyToOne para Usuario)

Pedido "1" -- "1" Cliente : feito por <
Pedido "1" -- "1" Endereco : entregue em >
Pedido "1" o-- "1..*" ItemPedido : contém (Composição)
ItemPedido "1" -- "1" Produto : refere-se a >
Produto "1" -- "0..*" Categoria : pertence a (ManyToMany)

Pedido "1" -- "1" Pagamento : pago com >
@enduml
```

## Tecnologias Utilizadas
O projeto utiliza as seguintes tecnologias principais:

Java (versão 21)
Spring Boot (versão 3.5.0)
Spring Web (MVC)
Spring Data JPA
Spring Boot DevTools
Thymeleaf com Layout Dialect (para templates web)
Spring Security Crypto (para hashing de senhas com BCrypt)
H2 Database (banco de dados em memória para desenvolvimento)
Spring Boot Starter Validation
Apache Maven (ferramenta de build)

---
## Autores
Este projeto foi desenvolvido por:

Alexandre Foppa
Arthur Portaluppi
Guilherme Perlasca
João Caumo
Kauã Xavier
