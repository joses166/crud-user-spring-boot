package br.com.josehamilton.crud.api.service;

import br.com.josehamilton.crud.api.entity.User;
import br.com.josehamilton.crud.api.exception.BusinessException;
import br.com.josehamilton.crud.api.repository.UserRepository;
import br.com.josehamilton.crud.api.service.impl.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    UserService userService;

    @MockBean
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        this.userService = new UserServiceImpl(userRepository);
    }

    @Test
    @DisplayName("Deve salvar um usuário.")
    public void createAnUser() {
        // Cenário
        User user = User.builder()
                        .fullname("Fulano")
                        .email("fulano@email.com")
                        .cpf("54737491004")
                        .build();
        User savedUser = User.builder()
                .id(1l)
                .fullname("Fulano")
                .email("fulano@email.com")
                .cpf("54737491004")
                .build();
        Mockito.when( userRepository.existsByEmail(Mockito.anyString()) ).thenReturn( false );
        Mockito.when( userRepository.existsByCpf(Mockito.anyString()) ).thenReturn( false );
        Mockito.when( userRepository.save( user ) ).thenReturn( savedUser );
        // Execução
        savedUser = this.userService.save( user );
        // Verificação
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getFullname()).isEqualTo(user.getFullname());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getCpf()).isEqualTo(user.getCpf());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar salvar um usuário com email já existente.")
    public void createAnUserExistentEmail() {
        // Cenário
        User user = User.builder()
                .fullname("Fulano")
                .email("fulano@email.com")
                .cpf("54737491004")
                .build();
        Mockito.when( userRepository.existsByEmail(Mockito.anyString()) ).thenReturn( true );
        // Execução
        Throwable throwable = Assertions.catchThrowable(() -> userService.save(user));
        // Verificações
        assertThat( throwable ).isInstanceOf( BusinessException.class ).hasMessage("O email já está cadastrado.");
        Mockito.verify( userRepository, Mockito.never() ).save(user);
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar salvar um usuário com cpf já existente.")
    public void createAnUserExistentCPF() {
        // Cenário
        User user = User.builder()
                .fullname("Fulano")
                .email("fulano@email.com")
                .cpf("54737491004")
                .build();
        Mockito.when( userRepository.existsByCpf(Mockito.anyString()) ).thenReturn( true );
        // Execução
        Throwable throwable = Assertions.catchThrowable(() -> userService.save(user));
        // Verificações
        assertThat( throwable ).isInstanceOf( BusinessException.class ).hasMessage("O cpf já está cadastrado.");
        Mockito.verify( userRepository, Mockito.never() ).save(user);
    }

    @Test
    @DisplayName("Deve retornar o usuário pelo id.")
    public void getUserByIdTest() {
        // Cenário
        Long id = 1l;
        User user = User.builder()
                .id(id)
                .fullname("Fulano")
                .email("fulano@email.com")
                .cpf("54737491004")
                .build();
        Mockito.when( userRepository.findById( id ) ).thenReturn( Optional.of( user ) );
        // Execução
        Optional<User> result = this.userService.getUserById(id);
        // Verificações
        assertThat( result.isPresent() );
        assertThat( result.get().getId() ).isEqualTo( id );
        assertThat( result.get().getFullname() ).isEqualTo( user.getFullname() );
        assertThat( result.get().getEmail() ).isEqualTo( user.getEmail() );
        assertThat( result.get().getCpf() ).isEqualTo( user.getCpf() );
    }

    @Test
    @DisplayName("Deve retornar vazio quando for pesquisar um usuário inexistente.")
    public void getInexistentUserByIdTest() {
        // Cenário
        Long id = 1l;
        User user = User.builder()
                .id(id)
                .fullname("Fulano")
                .email("fulano@email.com")
                .cpf("54737491004")
                .build();
        Mockito.when( userRepository.findById( id ) ).thenReturn( Optional.empty() );
        // Execução
        Optional<User> result = this.userService.getUserById(id);
        // Verificações
        assertThat( result.isPresent() ).isFalse();
    }

    @Test
    @DisplayName("Deve ser feita a remoção do usuário.")
    public void deleteAnUserTest() {
        // Cenário
        Long id = 1l;
        User user = User.builder()
                .id(id)
                .fullname("Fulano")
                .email("fulano@email.com")
                .cpf("54737491004")
                .build();
        // Execução
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> this.userService.delete(user));
        // Verificação
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }

    @Test
    @DisplayName("Deve ser feita a remoção do usuário.")
    public void deleteAnInexistentUserTest() {
        // Cenário
        User user = null;
        // Execução
        org.junit.jupiter.api.Assertions.assertThrows(BusinessException.class, () -> this.userService.delete(user));
        // Verificação
        Mockito.verify(userRepository, Mockito.never()).delete(user);
    }

    @Test
    @DisplayName("Deve alterar um usuário.")
    public void updateAnUserTest() {
        // Cenário
        Long id = 1l;
        User user = User.builder().id(1l).build();
        User userUpdated = User.builder().id(id).fullname("Fulano").email("email@email.com").cpf("12345678900").build();
        Mockito.when( this.userRepository.save( user ) ).thenReturn( userUpdated );
        // Execução
        User result = this.userService.update( user );
        // Verificações
        assertThat( result.getId() ).isEqualTo( id );
        assertThat( result.getFullname() ).isEqualTo( userUpdated.getFullname() );
        assertThat( result.getEmail() ).isEqualTo( userUpdated.getEmail() );
        assertThat( result.getCpf() ).isEqualTo( userUpdated.getCpf() );
    }

    @Test
    @DisplayName("Deve dar erro ao tentar alterar um usuário.")
    public void updateAnInexistentUserTest() {
        // Cenário
        User user = null;
        // Execução
        org.junit.jupiter.api.Assertions.assertThrows(BusinessException.class, () -> this.userService.update( user ));
        // Verificações
        Mockito.verify( this.userRepository, Mockito.never() ).save( user );
    }

}