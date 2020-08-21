package br.com.josehamilton.crud.api.controller;

import br.com.josehamilton.crud.api.controllers.UserController;
import br.com.josehamilton.crud.api.dtos.UserDTO;
import br.com.josehamilton.crud.api.entity.User;
import br.com.josehamilton.crud.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String USER_API = "/api/users";

    @Autowired
    MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Deve salvar um novo usuário.")
    public void createUserTest() throws Exception {
        // Cenário
        UserDTO userDTO = UserDTO.builder().fullname("Fulano").email("fulano@email.com").cpf("54737491004").build();
        String json = new ObjectMapper().writeValueAsString(userDTO);

        User user = User.builder().id(1l).fullname("Fulano").email("fulano@email.com").cpf("54737491004").build();
        BDDMockito.given( userService.save(Mockito.any(User.class)) ).willReturn( user );

        // Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USER_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // Verificações
        mvc.perform(request)
            .andExpect( status().isOk() )
            .andExpect( jsonPath("data.id").isNotEmpty() )
            .andExpect( jsonPath("data.fullname").value("Fulano") )
            .andExpect( jsonPath("data.email").value("fulano@email.com") )
            .andExpect( jsonPath("data.cpf").value("54737491004") )
        ;
    }

    @Test
    @DisplayName("Deve retornar um erro BAD REQUEST ao tentar cadastrar um novo usuário.")
    public void createInexistentUserTest() throws Exception {
        // Cenário
        String json = new ObjectMapper().writeValueAsString(new UserDTO());

        // Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USER_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // Verificações
        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", hasSize(3)) )
        ;
    }

    @Test
    @DisplayName("Deve retornar um erro BAD REQUEST ao tentar cadastrar um novo usuário.")
    public void createUserInvalidCPFTest() throws Exception {
        // Cenário
        UserDTO userDTO = UserDTO.builder().fullname("Fulano").email("fulano@email.com").cpf("12345678900").build();
        String json = new ObjectMapper().writeValueAsString(userDTO);

        // Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USER_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // Verificações
        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", hasSize(1)) )
        ;
    }

    @Test
    @DisplayName("Deve realizar uma pesquisa pelo id do usuário.")
    public void getUserByIdTest() throws Exception {
        // Cenário
        Long id = 1l;
        User user = User.builder().id(1l).fullname("Fulano").email("fulano@email.com").cpf("54737491004").build();
        BDDMockito.given( userService.getUserById(Mockito.anyLong()) ).willReturn( Optional.of(user) );
        // Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);
        // Verificações
        mvc.perform(request)
            .andExpect( status().isOk() )
            .andExpect( jsonPath("data.id").value(id) )
            .andExpect( jsonPath("data.fullname").value(user.getFullname()) )
            .andExpect( jsonPath("data.email").value(user.getEmail()) )
            .andExpect( jsonPath("data.cpf").value(user.getCpf()) )
        ;
    }

    @Test
    @DisplayName("Deve retornar erro de NOT FOUND ao tentar pesquisar usuário inexistente pelo id.")
    public void getInexistentUserByIdTest() throws Exception {
        // Cenário
        Long id = 1l;
        BDDMockito.given( userService.getUserById(Mockito.anyLong()) ).willReturn( Optional.empty() );
        // Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);
        // Verificações
        mvc
            .perform(request)
            .andExpect( status().isNotFound() )
        ;
    }

}
