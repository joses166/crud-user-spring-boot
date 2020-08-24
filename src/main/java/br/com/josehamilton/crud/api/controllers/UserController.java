package br.com.josehamilton.crud.api.controllers;

import br.com.josehamilton.crud.api.dtos.UserDTO;
import br.com.josehamilton.crud.api.entity.User;
import br.com.josehamilton.crud.api.responses.Response;
import br.com.josehamilton.crud.api.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Slf4j
@Api("API User")
public class UserController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @PostMapping
    @ApiOperation("Save a new user.")
    public ResponseEntity<Response<UserDTO>> create(@RequestBody @Valid UserDTO dto, BindingResult result) {
        // Log informado o que o método executa
        log.info("Na rota utilizada será feito um método POST para inserir um novo usuário.");
        // Variável instanciada de resposta
        Response<UserDTO> response = new Response<>();
        // Verificando se existe erro na requisição
        if ( result.hasErrors() ) {
            result.getAllErrors().forEach( error -> response.getErrors().add( error.getDefaultMessage() ) );
            return ResponseEntity.badRequest().body(response);
        }
        try {
            // Cadastrando usuário na base de dados
            User user = this.modelMapper.map(dto, User.class);
            user = this.userService.save(user);
            dto = this.modelMapper.map(user, UserDTO.class);
            response.setData(dto);
            // Retorno com o status 200 e os dados do usuário
            return ResponseEntity.ok(response);
        } catch ( Exception ex ) {
            response.getErrors().add( ex.getMessage() );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("{id}")
    @ApiOperation("Obtains details of an user.")
    public ResponseEntity<Response<UserDTO>> getUser(@PathVariable("id") Long id) {
        // Log informado o que o método executa
        log.info("Na rota utilizada será feito um método GET passando o ID como parâmetro para pesquisar um usuário.");
        // Variável instanciada de resposta
        Response<UserDTO> response = new Response<>();
        // Pesquisa do usuário pelo id
        User user = this.userService.getUserById( id ).orElse(null);
        // Caso não encontre o usuário entrará no if retornando erro de not found
        if ( user == null ) {
            return ResponseEntity.notFound().build();
        }
        // Caso encontre o usuário o valor será mapeado para um usuário dto para retorno
        UserDTO dto = this.modelMapper.map(user, UserDTO.class);
        response.setData(dto);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("{id}")
    @ApiOperation("Deletes an user by id.")
    public ResponseEntity<Response<UserDTO>> delete(@PathVariable("id") Long id) {
        // Log informado o que o método executa
        log.info("Na rota utilizada será feito um método DELETE passando o ID como parâmetro para excluir um usuário.");
        // Variável instanciada de resposta
        Response<UserDTO> response = new Response<>();
        // Pesquisa do usuário pelo id
        User user = this.userService.getUserById( id ).orElse(null);
        // Caso não encontre o usuário entrará no if retornando erro de not found
        if ( user == null ) {
            return ResponseEntity.notFound().build();
        }
        // Removendo o usuário
        this.userService.delete(user);
        // Retornando status de aceito
        return ResponseEntity.accepted().build();
    }

    @PutMapping("{id}")
    @ApiOperation("Updates an user by id.")
    public ResponseEntity<Response<UserDTO>> update(@PathVariable("id") Long id, @RequestBody UserDTO dto) {
        // Log informado o que o método executa
        log.info("Na rota utilizada será feito um método PUT passando o ID e o Usuário como parâmetro para alterar um usuário.");
        // Variável instanciada de resposta
        Response<UserDTO> response = new Response<>();
        // Pesquisa do usuário pelo id
        User foundUser = this.userService.getUserById(id).orElse(null);
        // Caso não encontre o usuário entrará no if retornando erro de not found
        if (foundUser == null) return ResponseEntity.notFound().build();
        try {
            // Trocando informações de atributos para atualizar usuário
            foundUser.setFullname(dto.getFullname());
            foundUser.setEmail(dto.getEmail());
            foundUser.setCpf(dto.getCpf());
            foundUser = this.userService.update(foundUser);
            UserDTO updatedUser = this.modelMapper.map(foundUser, UserDTO.class);
            response.setData(updatedUser);
            // Retornando usuário alterado com status ok
            return ResponseEntity.ok().body(response);
        } catch ( Exception ex ) {
            response.getErrors().add( ex.getMessage() );
            return ResponseEntity.badRequest().body(response);
        }
    }

}
