package com.example.server.controller;

import com.example.common.api.ContractApiContract;
import com.example.common.dto.ContractDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.server.service.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contracts")
public class ContractController implements ContractApiContract {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @Override
    @GetMapping
    public List<ContractDTO> listContracts() {
        return contractService.findAll();
    }

    @Override
    @GetMapping("/{id}")
    public Optional<ContractDTO> findById(@PathVariable Long id) {
        return contractService.findById(id);
    }

    @Override
    @PostMapping
    public ContractDTO create(@RequestBody ContractDTO contractDTO) {
        return contractService.create(contractDTO);
    }

    @Override
    @PutMapping("/{id}")
    public ContractDTO update(@PathVariable Long id, @RequestBody ContractDTO contractDTO) {
        return contractService.update(id, contractDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract not found"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boolean deleted = contractService.delete(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract not found");
        }
    }

    @Override
    @GetMapping("/{id}/history")
    public List<DocumentHistoryDTO> history(@PathVariable Long id) {
        return contractService.history(id);
    }
}
