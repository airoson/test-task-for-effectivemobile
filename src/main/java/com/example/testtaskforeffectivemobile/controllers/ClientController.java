package com.example.testtaskforeffectivemobile.controllers;

import com.example.testtaskforeffectivemobile.dtos.ClientDescription;
import com.example.testtaskforeffectivemobile.dtos.ClientSearchResponse;
import com.example.testtaskforeffectivemobile.dtos.InformationUpdateRequest;
import com.example.testtaskforeffectivemobile.entities.Client;
import com.example.testtaskforeffectivemobile.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name="Пользователи", description = "Позволяет получить информацию о пользователях")
public class ClientController {
    private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/client")
    @Operation(summary = "Информация о текущем пользователе", description = "Позволяет получить инофрмацию о текущем пользователе")
    public ResponseEntity<ClientSearchResponse> getCurrentClient(Principal principal){
        Client client = clientService.getClientByLogin(principal.getName());
        if(client != null){
            return ResponseEntity.ok(new ClientSearchResponse(List.of(clientService.getDescriptionForClient(client))));
        }else{
            return ResponseEntity.ok(new ClientSearchResponse("Client was deleted"));
        }
    }

    @GetMapping("/client/search")
    @Operation(summary = "Поиск пользователей", description = "Позволяет выполнить поиск пользователей по критериям")
    public ResponseEntity<ClientSearchResponse> searchClient(@RequestParam(required = false) @Parameter(description = "Номер телефона") String phone,
                                          @RequestParam(required = false) @Parameter(description = "Электронная почта") String email,
                                          @RequestParam(required = false) @Parameter(description = "ФИО") String fullName,
                                          @RequestParam(required = false) @Parameter(description = "Дата рождения") String birthDate,
                                          @RequestParam(required = false) @Parameter(description = "Номер страницы") Integer page,
                                          @RequestParam(required = false) @Parameter(description = "Рамер страницы") Integer size,
                                          @RequestParam(required = false) @Parameter(description = "Поле для сортировки") String sortBy){
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
    @Operation(summary = "Изменение данных текущего пользователя", description = "Позволяет изменить эл. почты и номера телефонов текущего пользователя")
    public ResponseEntity<ClientDescription> updateClient(@RequestBody InformationUpdateRequest updateRequest, Principal principal){
        Client client = clientService.updateClient(updateRequest, principal.getName());
        return ResponseEntity.ok(clientService.getDescriptionForClient(client));
    }
}
