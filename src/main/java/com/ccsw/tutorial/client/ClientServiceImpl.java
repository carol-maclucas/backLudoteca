package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.loan.LoanRepository;
import com.ccsw.tutorial.loan.model.Loan;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LoanRepository loanRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Client get(Long id) {
        return this.clientRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Client> findAll() {
        return (List<Client>) this.clientRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Client findByName(String name) {
        return this.clientRepository.findByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, ClientDto dto) {
        Client client;
        if (id == null) {
            client = new Client();
        } else {
            client = this.get(id);

            if (client == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
            }
        }
        Client existedClient = this.clientRepository.findByName(dto.getName());
        if (existedClient != null) {
            // Si estamos creando uno nuevo pero, el nombre ya existe
            // Si le cambio el nombre a un cliente con uno que ya existe en la BBDD
            // porque Id:3 Clara != Id:8 Clara
            if (id == null || !existedClient.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Client already exists");
            }
        }
        client.setName(dto.getName());
        this.clientRepository.save(client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws Exception {
        if (this.get(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        List<Loan> loans = this.loanRepository.findByClientId(id);

        if (!loans.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar un cliente con préstamos activos");
        }
        this.clientRepository.deleteById(id);
    }
}
