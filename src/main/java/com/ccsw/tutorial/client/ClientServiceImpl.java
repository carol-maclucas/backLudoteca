package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;

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
                throw new RuntimeException("Client not found");
            }
        }
        Client existedClient = this.clientRepository.findByName(dto.getName());
        if (existedClient != null) {
            // Si estamos creando uno nuevo pero, el nombre ya existe
            // Si le cambio el nombre a un cliente con uno que ya existe en la BBDD
            // porque Id:3 Clara != Id:8 Clara
            if (id == null || !existedClient.getId().equals(id)) {
                throw new RuntimeException("Client already exists");
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
            throw new Exception("Category not found");
        }
        this.clientRepository.deleteById(id);
    }
}
