package com.trabalho.cliqaqui.controller;

import com.trabalho.cliqaqui.dto.CartaoCreditoDTO;
import com.trabalho.cliqaqui.dto.PagamentoResponseDTO;
import com.trabalho.cliqaqui.model.Pagamento;
import com.trabalho.cliqaqui.model.PagamentoBoleto;
import com.trabalho.cliqaqui.model.PagamentoCartao;
import com.trabalho.cliqaqui.model.Pedido;
import com.trabalho.cliqaqui.model.StatusPagamento;
import com.trabalho.cliqaqui.repositories.PedidoRepository; // For fetching order details
import com.trabalho.cliqaqui.service.PagamentoService;

import jakarta.servlet.http.HttpSession; // For user session validation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.Optional;

@Controller
@RequestMapping("/pagamento")
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final PedidoRepository pedidoRepository; // To load pedido for display

    @Autowired
    public PagamentoController(PagamentoService pagamentoService, PedidoRepository pedidoRepository) {
        this.pagamentoService = pagamentoService;
        this.pedidoRepository = pedidoRepository;
    }

    // Helper to add common model attributes for user login state
    private void addUserLoginInfoToModel(Model model, HttpSession session) {
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        if (loggedInUserEmail != null) {
            model.addAttribute("isUserLoggedIn", true);
            model.addAttribute("loggedInUserEmail", loggedInUserEmail);
            model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
            model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));
        } else {
            model.addAttribute("isUserLoggedIn", false);
        }
    }

    @GetMapping("/pedido/{pedidoId}/escolher")
    public String escolherFormaPagamento(@PathVariable("pedidoId") Integer pedidoId,
                                         Model model,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        addUserLoginInfoToModel(model, session);
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para efetuar o pagamento.");
            return "redirect:/login";
        }

        Optional<Pedido> optionalPedido = pedidoRepository.findById(pedidoId);
        if (!optionalPedido.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pedido não encontrado.");
            return "redirect:/cliente/pedidos"; // Or some other appropriate page
        }
        Pedido pedido = optionalPedido.get();

        // Security check: Ensure the logged-in user owns this order
        if (!pedido.getCliente().getId().equals(loggedInUserId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não tem permissão para pagar este pedido.");
            return "redirect:/cliente/pedidos";
        }

        // Check if order already has a successful payment
        if (pedido.getPagamento() != null && pedido.getPagamento().getStatus() == StatusPagamento.APROVADO) {
             redirectAttributes.addFlashAttribute("infoMessage", "Este pedido já foi pago.");
             return "redirect:/cliente/pedidos/" + pedidoId; // Redirect to order details page
        }

        // Check if order can be paid (e.g. status is AGUARDANDO_PAGAMENTO or PENDENTE)
        // This logic can be expanded based on your order workflow
        if (pedido.getStatus() != com.trabalho.cliqaqui.model.StatusPedido.AGUARDANDO_PAGAMENTO &&
            pedido.getStatus() != com.trabalho.cliqaqui.model.StatusPedido.PENDENTE &&
            pedido.getStatus() != com.trabalho.cliqaqui.model.StatusPedido.FALHA_PAGAMENTO) {
            redirectAttributes.addFlashAttribute("errorMessage", "Este pedido não pode ser pago no momento (Status: " + pedido.getStatus() + ").");
            return "redirect:/cliente/pedidos/" + pedidoId;
        }


        model.addAttribute("pedido", pedido);
        model.addAttribute("cartaoCreditoDTO", new CartaoCreditoDTO()); // For the card form
        return "checkout/escolher-pagamento"; // Path to the new Thymeleaf template
    }

    @PostMapping("/pedido/{pedidoId}/cartao")
    public String processarPagamentoCartao(@PathVariable("pedidoId") Integer pedidoId,
                                           @ModelAttribute("cartaoCreditoDTO") CartaoCreditoDTO cartaoDto, // Ensure this matches the form object name
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes,
                                           Model model) { // Added Model for potentially re-rendering on validation failure
        addUserLoginInfoToModel(model, session); // For page layout if returning to form
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Por favor, faça login novamente.");
            return "redirect:/login";
        }

        Optional<Pedido> optionalPedido = pedidoRepository.findById(pedidoId);
        if (!optionalPedido.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pedido não encontrado.");
            return "redirect:/cliente/pedidos";
        }
        Pedido pedido = optionalPedido.get();

        if (!pedido.getCliente().getId().equals(loggedInUserId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não tem permissão para pagar este pedido.");
            return "redirect:/cliente/pedidos";
        }

        // Basic validation for DTO (more can be added with @Valid)
        if (cartaoDto.getNumeroCartao() == null || cartaoDto.getNumeroCartao().trim().isEmpty() ||
            cartaoDto.getNomeTitular() == null || cartaoDto.getNomeTitular().trim().isEmpty() ||
            cartaoDto.getMesValidade() == null || cartaoDto.getMesValidade().trim().isEmpty() ||
            cartaoDto.getAnoValidade() == null || cartaoDto.getAnoValidade().trim().isEmpty() ||
            cartaoDto.getCvv() == null || cartaoDto.getCvv().trim().isEmpty() ||
            cartaoDto.getNumeroParcelas() < 1) {

            redirectAttributes.addFlashAttribute("errorMessage", "Todos os campos do cartão são obrigatórios e as parcelas devem ser pelo menos 1.");
            // It's better to return to the form with errors displayed, rather than just redirecting
            // For simplicity in this step, redirecting. A more robust solution would repopulate the model and return the view.
            // model.addAttribute("pedido", pedido);
            // model.addAttribute("org.springframework.validation.BindingResult.cartaoCreditoDTO", ...); // Add binding errors
            // return "checkout/escolher-pagamento";
             return "redirect:/pagamento/pedido/" + pedidoId + "/escolher";
        }


        try {
            PagamentoCartao pagamentoCartao = pagamentoService.processarPagamentoCartao(pedidoId, cartaoDto);
            if (pagamentoCartao.getStatus() == StatusPagamento.APROVADO) {
                redirectAttributes.addFlashAttribute("successMessage", "Pagamento com cartão aprovado!");
                return "redirect:/cliente/pedidos/" + pedidoId; // Redirect to order details
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Pagamento com cartão recusado. Verifique os dados ou tente outra forma de pagamento.");
                return "redirect:/pagamento/pedido/" + pedidoId + "/escolher";
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao processar pagamento: " + e.getMessage());
            return "redirect:/pagamento/pedido/" + pedidoId + "/escolher";
        }
    }

    @PostMapping("/pedido/{pedidoId}/boleto")
    public String processarPagamentoBoleto(@PathVariable("pedidoId") Integer pedidoId,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes,
                                           Model model) {
        addUserLoginInfoToModel(model, session);
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Por favor, faça login novamente.");
            return "redirect:/login";
        }

        Optional<Pedido> optionalPedido = pedidoRepository.findById(pedidoId);
        if (!optionalPedido.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pedido não encontrado.");
            return "redirect:/cliente/pedidos";
        }
        Pedido pedido = optionalPedido.get();
        if (!pedido.getCliente().getId().equals(loggedInUserId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não tem permissão para pagar este pedido.");
            return "redirect:/cliente/pedidos";
        }

        // Check if order already has a successful payment or a pending boleto
        if (pedido.getPagamento() != null) {
            if(pedido.getPagamento().getStatus() == StatusPagamento.APROVADO) {
                 redirectAttributes.addFlashAttribute("infoMessage", "Este pedido já foi pago.");
                 return "redirect:/cliente/pedidos/" + pedidoId;
            }
            if(pedido.getPagamento().getStatus() == StatusPagamento.PENDENTE && pedido.getPagamento() instanceof PagamentoBoleto) {
                 redirectAttributes.addFlashAttribute("infoMessage", "Já existe um boleto pendente para este pedido.");
                 // Redirect to a page that shows the existing boleto
                 return "redirect:/pagamento/pedido/" + pedidoId + "/boleto/ver";
            }
        }


        try {
            PagamentoBoleto pagamentoBoleto = pagamentoService.processarPagamentoBoleto(pedidoId);
            redirectAttributes.addFlashAttribute("successMessage", "Boleto gerado com sucesso!");
            // Store boleto details in flash attributes to display on the next page
            redirectAttributes.addFlashAttribute("codigoBarrasBoleto", pagamentoBoleto.getCodigoBarras());
            redirectAttributes.addFlashAttribute("urlBoleto", pagamentoBoleto.getUrlBoleto());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            redirectAttributes.addFlashAttribute("dataVencimentoBoleto", sdf.format(pagamentoBoleto.getDataVencimento()));

            return "redirect:/pagamento/pedido/" + pedidoId + "/boleto/ver"; // Redirect to a page to view the boleto
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao gerar boleto: " + e.getMessage());
            return "redirect:/pagamento/pedido/" + pedidoId + "/escolher";
        }
    }

    // GET endpoint to view generated boleto details
    @GetMapping("/pedido/{pedidoId}/boleto/ver")
    public String verBoleto(@PathVariable("pedidoId") Integer pedidoId,
                            Model model,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        addUserLoginInfoToModel(model, session);
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para ver os detalhes do boleto.");
            return "redirect:/login";
        }

        Optional<Pedido> optionalPedido = pedidoRepository.findById(pedidoId);
        if (!optionalPedido.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pedido não encontrado.");
            return "redirect:/cliente/pedidos";
        }
        Pedido pedido = optionalPedido.get();

        if (!pedido.getCliente().getId().equals(loggedInUserId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não tem permissão para ver este boleto.");
            return "redirect:/cliente/pedidos";
        }

        if (pedido.getPagamento() == null || !(pedido.getPagamento() instanceof PagamentoBoleto) || pedido.getPagamento().getStatus() != StatusPagamento.PENDENTE) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nenhum boleto pendente encontrado para este pedido.");
            return "redirect:/pagamento/pedido/" + pedidoId + "/escolher";
        }

        PagamentoBoleto boleto = (PagamentoBoleto) pedido.getPagamento();
        model.addAttribute("pedido", pedido);
        model.addAttribute("boleto", boleto); // Pass the boleto object itself

        // If attributes were flashed, they are already available.
        // If accessing directly (e.g. bookmarked URL), we load from 'boleto' object.
        if (!model.containsAttribute("codigoBarrasBoleto")) { // Check if not already flashed
             model.addAttribute("codigoBarrasBoleto", boleto.getCodigoBarras());
             model.addAttribute("urlBoleto", boleto.getUrlBoleto());
             SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
             model.addAttribute("dataVencimentoBoleto", sdf.format(boleto.getDataVencimento()));
        }

        return "checkout/pagamento-boleto"; // Path to Thymeleaf template to display boleto
    }

    // Simulated Webhook Endpoints (for testing/simulation purposes)
    // In a real application, these would be POST endpoints, secured, and called by the payment gateway.

    @GetMapping("/simular-webhook/confirmar/{pagamentoId}")
    @ResponseBody // Or return a view/message
    public String simularConfirmacaoPagamento(@PathVariable("pagamentoId") Integer pagamentoId, RedirectAttributes redirectAttributes) {
        try {
            Pagamento pagamento = pagamentoService.confirmarPagamento(pagamentoId);
            // In a real scenario, you might not redirect but just return a 200 OK to the gateway.
            // For simulation, a message is useful.
            // redirectAttributes.addFlashAttribute("successMessage", "Pagamento ID " + pagamentoId + " confirmado via simulação de webhook.");
            // return "redirect:/cliente/pedidos/" + pagamento.getPedido().getId();
            return "Simulação: Pagamento ID " + pagamentoId + " confirmado para Pedido ID " + pagamento.getPedido().getId() + ". Status Pagamento: " + pagamento.getStatus() + ", Status Pedido: " + pagamento.getPedido().getStatus();
        } catch (RuntimeException e) {
            // redirectAttributes.addFlashAttribute("errorMessage", "Erro ao simular confirmação de pagamento: " + e.getMessage());
            // return "redirect:/"; // Or some admin page
            return "Erro ao simular confirmação para Pagamento ID " + pagamentoId + ": " + e.getMessage();
        }
    }

    @GetMapping("/simular-webhook/falhar/{pagamentoId}")
    @ResponseBody // Or return a view/message
    public String simularFalhaPagamento(@PathVariable("pagamentoId") Integer pagamentoId, RedirectAttributes redirectAttributes) {
        try {
            Pagamento pagamento = pagamentoService.cancelarOuFalharPagamento(pagamentoId);
            // redirectAttributes.addFlashAttribute("infoMessage", "Pagamento ID " + pagamentoId + " marcado como falha/cancelado via simulação de webhook.");
            // return "redirect:/cliente/pedidos/" + pagamento.getPedido().getId();
             return "Simulação: Pagamento ID " + pagamentoId + " falhou para Pedido ID " + pagamento.getPedido().getId() + ". Status Pagamento: " + pagamento.getStatus() + ", Status Pedido: " + pagamento.getPedido().getStatus();
        } catch (RuntimeException e) {
            // redirectAttributes.addFlashAttribute("errorMessage", "Erro ao simular falha de pagamento: " + e.getMessage());
            // return "redirect:/"; // Or some admin page
            return "Erro ao simular falha para Pagamento ID " + pagamentoId + ": " + e.getMessage();
        }
    }
}
