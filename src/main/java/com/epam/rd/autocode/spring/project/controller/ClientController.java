package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clients")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @GetMapping
    public String allClients(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "clients/client_list";
    }

    @PostMapping("/toggle-block")
    public String blockClient(@RequestParam String email) {
        clientService.toggleBlockByEmail(email);
        return "redirect:/clients";
    }
}
