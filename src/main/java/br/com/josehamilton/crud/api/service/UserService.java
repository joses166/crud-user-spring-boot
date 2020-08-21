package br.com.josehamilton.crud.api.service;

import br.com.josehamilton.crud.api.entity.User;

import java.util.Optional;

public interface UserService {

    User save(User user);

    Optional<User> getUserById(Long id);
}
