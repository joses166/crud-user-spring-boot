package br.com.josehamilton.crud.api.service;

import br.com.josehamilton.crud.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    User save(User user);

    Optional<User> getUserById(Long id);

    void delete(User user);

    User update(User user);

    Page<User> find(User filter, Pageable pageRequest);
}
