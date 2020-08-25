package br.com.josehamilton.crud.api.service.impl;

import br.com.josehamilton.crud.api.entity.User;
import br.com.josehamilton.crud.api.exception.BusinessException;
import br.com.josehamilton.crud.api.repository.UserRepository;
import br.com.josehamilton.crud.api.service.UserService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        // Verifica se o usuário existe
        if (user == null || user.getId() == null) {
            throw new BusinessException("Usuário que está tentando ser removido não existe.");
        }
        // Excluindo usuário que foi passado como parâmetro
        this.userRepository.delete(user);
    }

    @Override
    public User update(User user) {
        // Verifica se o usuário existe
        if (user == null || user.getId() == null) {
            throw new BusinessException("Usuário que está tentando ser alterado não existe.");
        }
        // Alterando usuário que foi passado como parâmetro
        return this.userRepository.save( user );
    }

    @Override
    public Page<User> find(User filter, Pageable pageRequest) {
        // Configurando parâmetros dentro de classe Example
        Example<User> example = Example.of(
                filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );
        // Faz busca com parâmetros e paginação
        return this.userRepository.findAll(example, pageRequest);
    }

}
