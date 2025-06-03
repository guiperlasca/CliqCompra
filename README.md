# CliqCompra: Sistema Integrado de Mediação de Compras e Vendas

## Sumário
- [Sobre o Projeto](#sobre-o-projeto)
- [Inspiração e Objetivo Final](#inspiração-e-objetivo-final)
- [Atores do Sistema](#atores-do-sistema)
- [Funcionalidades Implementadas e Previstas](#funcionalidades-implementadas-e-previstas)
- [Principais Casos de Uso (Conforme Documentação Original)](#principais-casos-de-uso-conforme-documentação-original)
- [Requisitos Não Funcionais (Visão Geral)](#requisitos-não-funcionais-visão-geral)
- [Modelo de Dados (UML)](#modelo-de-dados-uml)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Como Executar](#como-executar)
- [Autores](#autores)
- [Instituição](#instituição)

## Sobre o Projeto
CliqCompra é uma plataforma de e-commerce em desenvolvimento, construída com Java e Spring Boot[cite: 2]. O projeto, originalmente intitulado "Sistema Integrado de Mediação de Compras e Vendas para Usuários em Ambiente Virtual"[cite: 1], visa criar um ambiente online para a compra e venda de produtos, servindo como um intermediário entre usuários[cite: 2]. A ideia é facilitar as transações comerciais de forma simples e eficiente[cite: 2], simulando uma estrutura básica de e-commerce[cite: 4].

Este projeto é parte de uma iniciativa acadêmica da Universidade Ritter dos Reis, do curso de Ciências da Computação (2024)[cite: 1], com foco na simplicidade e na aplicação prática dos conceitos estudados[cite: 6].

## Inspiração e Objetivo Final
O projeto é **inspirado em plataformas de sucesso de intermediação de vendas, como a OLX**[cite: 2]. O objetivo final é evoluir o CliqCompra para um marketplace completo e robusto, onde usuários (tanto consumidores quanto vendedores – Pessoas Físicas ou Jurídicas) possam anunciar, descobrir e negociar uma ampla variedade de produtos de forma intuitiva e segura[cite: 2]. Queremos facilitar a conexão direta entre quem quer vender e quem quer comprar, promovendo um ecossistema de comércio dinâmico e acessível[cite: 2]. O objetivo original do sistema é permitir a interação entre compradores e vendedores em um ambiente virtual, facilitando a divulgação de produtos, realização de pedidos e gerenciamento de pagamentos[cite: 3].

## Atores do Sistema
Conforme a documentação original e o desenvolvimento atual:
* **Usuário Visitante**: Aquele que acessa o sistema sem estar autenticado[cite: 51].
* **Usuário Cadastrado (Cliente ou Vendedor)**: Pessoa que possui conta no sistema[cite: 52]. O sistema atualmente tem um fluxo principal de cadastro focado no perfil de `Cliente`[cite: 2], mas a estrutura de `Usuario.java`, `Cliente.java`[cite: 21], e `Vendedor.java` [cite: 36] suporta essa distinção.
* **Sistema de Pagamento (Previsto)**: Serviço externo ou simulado que processaria os pagamentos[cite: 54]. Modelos como `Pagamento.java`[cite: 25], `PagamentoBoleto.java`[cite: 26], e `PagamentoCartao.java` [cite: 27] indicam essa intenção.

## Funcionalidades Implementadas e Previstas

**Funcionalidades Atuais (Conforme `guiperlasca/cliqcompra/CliqCompra-2b55bac3ccc2e61c268c5d2fdf052eb52f4ff5f7/README.md` e código-fonte):**
* **Gerenciamento de Usuários:**
    * Cadastro de novos usuários (foco no perfil de `Cliente`)[cite: 2], conforme `WebController.java` (método `processRegistration`).
    * Login e Logout de usuários com persistência de sessão[cite: 2], via `WebController.java` (métodos `processLogin`, `logout`).
    * Hashing de senhas para segurança utilizando BCrypt[cite: 2], implementado em `PasswordService.java`[cite: 47].
* **Gerenciamento de Produtos:**
    * Listagem de todos os produtos disponíveis para compra[cite: 2], via `WebController.java` (método `produtos`) e `ProdutoController.java` (`listarTodosProdutos`).
    * Permite que usuários logados adicionem novos produtos ao sistema, associando o produto ao usuário que o cadastrou[cite: 2]. Implementado em `WebController.java` (método `processAddProduct`). O `Produto.java` possui associação com `Usuario.java`[cite: 31].
    * Upload de fotos para os produtos, que são armazenadas localmente e exibidas nas listagens[cite: 2]. `WebController.java` (método `processAddProduct`) gerencia `fotoFile`.
    * Visualização dos produtos anunciados pelo próprio usuário na seção "Meus Produtos"[cite: 2], via `WebController.java` (método `showMyProducts`).
* **Carrinho de Compras:**
    * Adição de produtos ao carrinho de compras[cite: 2], via `WebController.java` (método `addToCart`).
    * Visualização detalhada do carrinho[cite: 2], via `WebController.java` (método `viewCart`).
    * Atualização da quantidade de itens no carrinho[cite: 2], via `WebController.java` (método `updateCartItem`).
    * Remoção de itens do carrinho[cite: 2], via `WebController.java` (método `removeCartItem`).
    * O carrinho de compras é mantido por sessão de usuário[cite: 2], configurado em `ShoppingCartService.java` com `@Scope(value = WebApplicationContext.SCOPE_SESSION)`[cite: 50].
* **Processo de Compra (Checkout):**
    * Seleção de endereço de entrega a partir dos endereços previamente cadastrados pelo `Cliente`[cite: 2]. `WebController.java` (método `showAddressSelectionPage`).
    * Gerenciamento de múltiplos endereços pelo `Cliente` (Adicionar e Listar)[cite: 2]. `WebController.java` (métodos `showAddAddressForm`, `processAddAddress`, `showMyAddressesPage`).
    * Página de confirmação dos detalhes do pedido antes da finalização[cite: 2]. `WebController.java` (método `showConfirmOrderDetailsPage`).
    * Finalização do pedido, com registro no sistema e limpeza do carrinho[cite: 2]. `WebController.java` (método `placeOrder`). O pedido é salvo com status inicial PENDENTE[cite: 10, 28].
* **Histórico de Compras:**
    * Clientes podem visualizar o histórico de seus pedidos realizados[cite: 2]. `WebController.java` (método `showMyOrders`).
    * Página de confirmação/detalhamento do pedido acessível após a compra ou pelo histórico[cite: 2]. `WebController.java` (método `showOrderConfirmationPage`).
* **API REST:**
    * Endpoints básicos foram criados para `Pedidos`, `Produtos` e `Usuários`, permitindo interações via HTTP[cite: 2]. Ver `PedidoController.java`[cite: 7], `ProdutoController.java`[cite: 8], `UsuarioController.java`[cite: 9].

**Funcionalidades Previstas (Baseado na Documentação Original e Modelos):**
* **Categorização de Produtos**: O sistema deve permitir que um produto pertença a uma ou mais categorias[cite: 15, 16]. O modelo `Produto.java` [cite: 31] inclui `List<Categoria> categorias`, e `Categoria.java` [cite: 20] está definido.
* **Descontos**: Permitir aplicar desconto individual por produto no pedido[cite: 19, 20]. O modelo `ItemPedido.java` [cite: 24] possui `descontoAplicado`.
* **Múltiplas Formas de Pagamento**: Permitir pagamento por boleto ou cartão de crédito[cite: 25, 26]. Os modelos `PagamentoBoleto.java` [cite: 26] e `PagamentoCartao.java` [cite: 27] existem.
    * Registrar data de vencimento e pagamento para boleto[cite: 27, 28]. O modelo `PagamentoBoleto.java` [cite: 26] inclui `dataVencimento`.
    * Permitir parcelamento em cartão de crédito[cite: 29, 30]. O modelo `PagamentoCartao.java` [cite: 27] inclui `numeroParcelas`.
* **Status de Pagamento**: Registrar o estado do pagamento (pendente, quitado ou cancelado)[cite: 30, 31]. O modelo `Pagamento.java` [cite: 25] possui `StatusPagamento`[cite: 32].

## Principais Casos de Uso (Conforme Documentação Original `CLIQCOMPRA DOC.docx`)
* **UC01 - Cadastrar Usuário**: Permite que o visitante crie uma conta no sistema[cite: 55].
* **UC02 - Fazer Login**: Permite que o usuário cadastrado acesse o sistema[cite: 57].
* **UC03 - Editar Perfil**: Permite ao usuário alterar seus dados pessoais[cite: 58]. (Funcionalidade básica de cadastro existe, edição de perfil pode ser uma melhoria futura).
* **UC04 - Cadastrar Produto**: Usuários (atualmente qualquer logado, originalmente Vendedores) podem adicionar produtos[cite: 59].
* **UC05 - Pesquisar Produtos**: Usuários podem buscar produtos[cite: 59]. (Listagem implementada, busca/filtros avançados podem ser melhorias).
* **UC06 - Adicionar Produto ao Carrinho**: Usuário escolhe produtos e define a quantidade[cite: 60].
* **UC07 - Realizar Pedido**: Finaliza o pedido, selecionando produtos, endereço e aplicando descontos[cite: 61].
    * *Inclui*: UC08 - Selecionar Forma de Pagamento[cite: 62]. (Seleção explícita de forma de pagamento no UI é uma melhoria futura).
* **UC09 - Emitir Boleto**: Emite boleto com data de vencimento e registra pagamento[cite: 63]. (Previsto pelo modelo).
* **UC10 - Registrar Pagamento com Cartão**: Define parcelas e confirma transação[cite: 64]. (Previsto pelo modelo).
* **UC11 - Acompanhar Pedido**: Usuário pode consultar os pedidos realizados e status[cite: 65].

## Requisitos Não Funcionais (Visão Geral da Documentação Original `CLIQCOMPRA DOC.docx`)
* **Usabilidade**: Interface simples e fácil de usar[cite: 37, 38].
* **Compatibilidade**: Funcionar em navegadores modernos (Chrome, Firefox, Edge)[cite: 38, 39].
* **Responsividade**: Funcionamento básico em celulares e computadores[cite: 40, 41].
* **Segurança**:
    * Senhas armazenadas de forma segura (criptografadas)[cite: 42, 43]. (Implementado com BCrypt [cite: 2, 47]).
    * Validação de campos obrigatórios (CPF/CNPJ, e-mail)[cite: 44].
    * Armazenamento seguro de informações importantes (pedidos, pagamentos)[cite: 49, 50].
* **Desempenho**: Carregamento rápido de páginas principais (até 3 segundos)[cite: 45, 46]. (Prioridade Média [cite: 46]).
* **Manutenibilidade**: Código organizado para facilitar manutenção[cite: 47, 48]. (Estrutura Spring Boot MVC e services).

## Modelo de Dados (UML)
A estrutura do modelo de dados do sistema está representada no diagrama UML abaixo (conforme `guiperlasca/cliqcompra/CliqCompra-2b55bac3ccc2e61c268c5d2fdf052eb52f4ff5f7/README.md`), que foi concebido para suportar as funcionalidades atuais e futuras da plataforma:

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
Vendedor "1" -- "0..*" Produto : cadastra < (Relação corrigida, Produto é cadastrado POR um Usuario/Vendedor)
' Ou melhor: Usuario "1" -- "0..*" Produto : cadastra > (Produto tem um ManyToOne para Usuario)

Pedido "1" -- "1" Cliente : feito por <
Pedido "1" -- "1" Endereco : entregue em >
Pedido "1" o-- "1..*" ItemPedido : contém (Composição)
ItemPedido "1" -- "1" Produto : refere-se a >
Produto "1" -- "0..*" Categoria : pertence a (ManyToMany)

Pedido "1" -- "1" Pagamento : pago com >
@enduml
