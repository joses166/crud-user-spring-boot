package br.com.josehamilton.crud.api.service.impl;

import br.com.josehamilton.crud.api.entity.User;
import br.com.josehamilton.crud.api.exception.BusinessException;
import br.com.josehamilton.crud.api.repository.UserRepository;
import br.com.josehamilton.crud.api.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        // Verificação de email se já existe cadastrado na base de dados
        if ( userRepository.existsByEmail(user.getEmail()) ) {
            throw new BusinessException("O email já está cadastrado.");
        }
        // Verificação de cpf se já existe cadastrado na base de dados
        if ( userRepository.existsByCpf(user.getCpf()) ) {
            throw new BusinessException("O cpf já está cadastrado.");
        }
        // Faz o salvamento da informação
        return this.userRepository.save( user );
    }

    @Override
    public Optional<User> getUserById(Long id) {
        // Retorna a classe User dentro de um Optional pelo id
        return this.userRepository.findById(id);
    }

    @Override
    public void delete(User user) {
        // Excluindo usuário que foi passado como parâmetro

    }

}
