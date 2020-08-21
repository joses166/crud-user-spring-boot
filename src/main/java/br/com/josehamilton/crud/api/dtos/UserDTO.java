package br.com.josehamilton.crud.api.dtos;

import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    @NotEmpty
    private String fullname;

    @NotEmpty
    @CPF
    private String cpf;

    @NotEmpty
    private String email;

}
