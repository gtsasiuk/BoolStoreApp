package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/clients")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @GetMapping
    public String allClients(@ModelAttribute("filter") UserFilterDTO filter, Model model) {
        log.info("Client list requested");
        Page<ClientDTO> clients = clientService.getAllClients(filter);
        model.addAttribute("clients",clients);
        return "clients/client_list";
    }

    @PostMapping("/toggle-block")
    public String blockClient(@ModelAttribute("filter") UserFilterDTO filter, @RequestParam String email) {
        log.warn("Toggle block requested for client email={}", email);
        clientService.toggleBlockByEmail(email);
        return "redirect:/clients?" + filter.toQueryString();
    }
}
