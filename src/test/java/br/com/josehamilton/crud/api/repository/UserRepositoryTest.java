package br.com.josehamilton.crud.api.repository;

import br.com.josehamilton.crud.api.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve salvar um usuário.")
    public void createAnUserTest() {
        // Cenário
        User user = User.builder().fullname("Fulano").email("fulano@email.com").cpf("54737491004").build();
        // Execução
        User savedUser = this.userRepository.save(user);
        // Verificações
        assertThat( savedUser.getId() ).isNotNull();
        assertThat( savedUser.getFullname() ).isEqualTo( user.getFullname() );
        assertThat( savedUser.getEmail() ).isEqualTo( user.getEmail() );
        assertThat( savedUser.getCpf() ).isEqualTo( user.getCpf() );
    }

    @Test
    @DisplayName("Deve retornar que existe um usuário com o email.")
    public void existsAnUserWithEmailTest() {
        // Cenário
        User user = createAndPersistUser();
        // Execução
        boolean exists = this.userRepository.existsByEmail("fulano@email.com");
        // Verificações
        assertThat( exists ).isTrue();
    }

    @Test
    @DisplayName("Deve retornar que existe um usuário com o cpf.")
    public void existsAnUserWithCpfTest() {
        // Cenário
        User user = createAndPersistUser();
        // Execução
        boolean exists = this.userRepository.existsByCpf("54737491004");
        // Verificações
        assertThat( exists ).isTrue();
    }

    @Test
    @DisplayName("Deve retornar o usuário pelo id.")
    public void findUserByIdTest() {
        // Cenário
        User user = createAndPersistUser();
        // Execução
        Optional<User> result = this.userRepository.findById(user.getId());
        // Verificações
        assertThat( result.isPresent() );
        assertThat( result.get().getId() ).isEqualTo( user.getId() );
        assertThat( result.get().getFullname() ).isEqualTo( user.getFullname() );
        assertThat( result.get().getEmail() ).isEqualTo( user.getEmail() );
        assertThat( result.get().getCpf() ).isEqualTo( user.getCpf() );
    }

    @Test
    @DisplayName("Deve retornar vazio quando pesquisar usuário inexistente.")
    public void findInexistentUserByIdTest() {
        // Cenário
        Long id = 1l;
        // Execução
        Optional<User> result = this.userRepository.findById(id);
        // Verificações
        assertThat( result.isPresent() ).isFalse();
    }

    @Test
    @DisplayName("Deve remover um usuário.")
    public void deleteAnUserTest() {
        // Cenário
        User userSaved = this.createAndPersistUser();
        User foundBook = this.entityManager.find(User.class, userSaved.getId());
        // Execução
        this.userRepository.delete(foundBook);
        // Verificações
        User deletedUser = this.entityManager.find(User.class, userSaved.getId());
        assertThat( deletedUser ).isNull();
    }

    public User createAndPersistUser() {
        User user = User.builder().fullname("Fulano").email("fulano@email.com").cpf("54737491004").build();
        entityManager.persist(user);
        return user;
    }

}
