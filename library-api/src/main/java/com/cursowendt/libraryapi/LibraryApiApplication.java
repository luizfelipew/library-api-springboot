package com.cursowendt.libraryapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

//    @Autowired
//    private EmailService emailService;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
//
//    @Bean
//    public CommandLineRunner runner() {
//        return args -> {
//            List<String> emails = Arrays.asList("library-api-6128fe@inbox.mailtrap.io");
//            emailService.sendMails("Testando serviço de emails.", emails);
//            System.out.println("EMAILS ENVIADOS!!!");
//        };
//    }

//    @Scheduled(cron = "0 4 19 1/1 * ?")
//    public void testeAgendamentoTarefas() {
//		System.out.println("AGENDAMENTO DE TAREFAS FUNCIONANDO COM SUCESSO!");
//    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryApiApplication.class, args);
    }

}
