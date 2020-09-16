package com.cursowendt.libraryapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

//    @Scheduled(cron = "0 4 19 1/1 * ?")
//    public void testeAgendamentoTarefas() {
//		System.out.println("AGENDAMENTO DE TAREFAS FUNCIONANDO COM SUCESSO!");
//    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryApiApplication.class, args);
    }

}
