package br.com.josehamilton.crud.api.config;

import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("br.com.josehamilton.crud.api.controllers"))
                    .paths(PathSelectors.any())
                    .build()
                    .apiInfo(apiInfo());
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                        .version("1.0.0")
                        .title("API de Usuários")
                        .description("Esta API foi construída para fazer as transações de CRUD de um usuário.")
                        .contact(contact())
                        .build()
                ;
    }

    public Contact contact() {
        return new Contact(
                "José Hamilton Martins Leite",
                "https://github.com/joses166",
                "josehamiltonmartinsleite@gmail.com"
        );
    }

}
