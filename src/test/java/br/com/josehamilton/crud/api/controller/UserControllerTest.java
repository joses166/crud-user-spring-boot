package br.com.josehamilton.crud.api.controller;

import br.com.josehamilton.crud.api.controllers.UserController;
import br.com.josehamilton.crud.api.dtos.UserDTO;
import br.com.josehamilton.crud.api.entity.User;
import br.com.josehamilton.crud.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        UserDTO userDTO = createAnUserDTO();
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
        UserDTO userDTO = createAnUserDTO();
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

    @Test
    @DisplayName("Deve ser removido um usuário a partir do id.")
    public void deleteUserByIdTest() throws Exception {
        // Cenário
        Long id = 1l;
        User user = User.builder().id(1l).fullname("Fulano").email("fulano@email.com").cpf("54737491004").build();
        BDDMockito.given( userService.getUserById(id) ).willReturn( Optional.of(user) );
        // Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(USER_API.concat("/" + id)).accept(MediaType.APPLICATION_JSON);
        // Verificações
        mvc.perform( request )
            .andExpect( status().isAccepted() );
    }

    @Test
    @DisplayName("Deve retornar erro not found ao tentar remover usuário inexistente.")
    public void deleteInexistentUserTest() throws Exception {
        // Cenário
        Long id = 1l;
        BDDMockito.given( userService.getUserById(id) ).willReturn( Optional.empty() );
        // Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(USER_API.concat("/" + id)).accept(MediaType.APPLICATION_JSON);
        // Verificações
        mvc.perform( request )
                .andExpect( status().isNotFound() );
    }

    @Test
    @DisplayName("Deve alterar um usuário.")
    public void updateUserTest() throws Exception {
        // Cenário
        Long id = 1l;
        UserDTO dto = createAnUserDTO();
        String json = new ObjectMapper().writeValueAsString( dto );

        User updatingUser = User.builder().id(1l).fullname("Fulano").email("fulano@email.com").cpf("54737491004").build();
        User updatedUser = User.builder().id(1l).fullname("Fulano alterado").email("fulanoalterado@email.com").cpf("54737491004").build();
        BDDMockito.given( this.userService.getUserById(id) ).willReturn( Optional.of(updatingUser) );
        BDDMockito.given( this.userService.update(updatingUser) ).willReturn(updatedUser);
        // Execuções
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(USER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        // Verificações
        mvc.perform(request)
                .andExpect( status().isOk() )
                .andExpect( jsonPath("data.id").value( updatedUser.getId() ) )
                .andExpect( jsonPath("data.fullname").value( updatedUser.getFullname() ) )
                .andExpect( jsonPath("data.email").value( updatedUser.getEmail() ) )
                .andExpect( jsonPath("data.cpf").value( updatedUser.getCpf() ) )
        ;
    }

    @Test
    @DisplayName("Deve alterar um usuário.")
    public void updateInexistentUserTest() throws Exception {
        // Cenário
        Long id = 1l;
        User updatedUser = User.builder().id(1l).fullname("Fulano alterado").email("fulanoalterado@email.com").cpf("54737491004").build();
        String json = new ObjectMapper().writeValueAsString( updatedUser );
        BDDMockito.given( this.userService.getUserById(id) ).willReturn( Optional.empty() );
        // Execuções
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(USER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        // Verificações
        mvc
            .perform(request)
            .andExpect( status().isNotFound() )
        ;
    }

    @Test
    @DisplayName("Deve listar dados de todos os usuários de acordo com os parâmetros informados.")
    public void getByParamsTest() throws Exception {
        // Cenário
        User user = User.builder().fullname("Fulano").email("fulano@email.com").cpf("54737491004").build();
        String parameters = String.format("?fullname=%s&email=%s&cpf=%s&page=0&size=20", user.getFullname(), user.getEmail(), user.getCpf());
        List<User> list = Arrays.asList( user );
        BDDMockito.given( userService.find( Mockito.any(User.class), Mockito.any(Pageable.class) )  )
                .willReturn( new PageImpl<User>( list, PageRequest.of(0, 20), 1 ) );

        // Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API.concat(parameters))
                .accept(MediaType.APPLICATION_JSON);

        // Verificações
        mvc.perform(request)
                .andExpect( status().isOk() )
                .andExpect( jsonPath("data.content", hasSize(1)) )
                .andExpect( jsonPath("data.totalElements").value(1) )
                .andExpect( jsonPath("data.pageable.pageSize").value(20) )
                .andExpect( jsonPath("data.pageable.pageNumber").value(0) )
        ;

    }

    public UserDTO createAnUserDTO() {
        return UserDTO.builder().fullname("Fulano").email("fulano@email.com").cpf("54737491004").build();
    }

}
