package br.com.josehamilton.crud.api.controllers;

import br.com.josehamilton.crud.api.dtos.UserDTO;
import br.com.josehamilton.crud.api.entity.User;
import br.com.josehamilton.crud.api.responses.Response;
import br.com.josehamilton.crud.api.service.UserService;
import lombok.RequiredArgsConstructor;
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
public class UserController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @PostMapping
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

}
