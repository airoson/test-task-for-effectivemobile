package com.example.testtaskforeffectivemobile.controllers;

import com.example.testtaskforeffectivemobile.dtos.ClientSearchResponse;
import com.example.testtaskforeffectivemobile.dtos.InformationUpdateRequest;
import com.example.testtaskforeffectivemobile.entities.Client;
import com.example.testtaskforeffectivemobile.services.ClientService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ClientController {
    private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/client")
    public ResponseEntity<?> getCurrentClient(Principal principal){
        Client client = clientService.getClientByLogin(principal.getName());
        if(client != null){
            return ResponseEntity.ok(clientService.getDescriptionForClient(client));
        }else{
            return ResponseEntity.ok(new ClientSearchResponse("Client was deleted"));
        }
    }

    @GetMapping("/client/search")
    public ResponseEntity<?> searchClient(@RequestParam(required = false) String phone,
                                          @RequestParam(required = false) String email,
                                          @RequestParam(required = false) String fullName,
                                          @RequestParam(required = false) String birthDate,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size,
                                          @RequestParam(required = false) String sortBy){
        Pageable pageable;
        if(page == null ^ size == null){
            return ResponseEntity.ok(new ClientSearchResponse("page and size must be both present in order to perform pagination"));
        }else if(page != null){
            if(sortBy != null)
                pageable = PageRequest.of(page, size, Sort.by(sortBy));
            else
                pageable = PageRequest.of(page, size);
        }else{
            if(sortBy != null)
                pageable = Pageable.unpaged(Sort.by(sortBy));
            else
                pageable = Pageable.unpaged();
        }
        List<Client> clients = clientService.getClientByCustomParams(phone, email, fullName, birthDate, pageable);
        return ResponseEntity.ok(new ClientSearchResponse(clients.stream()
                .map(client -> clientService.getDescriptionForClient(client))
                .toList()));
    }

    @PatchMapping("/client")
    public ResponseEntity<?> updateClient(@RequestBody InformationUpdateRequest updateRequest, Principal principal){
        Client client = clientService.updateClient(updateRequest, principal.getName());
        return ResponseEntity.ok(clientService.getDescriptionForClient(client));
    }
}
