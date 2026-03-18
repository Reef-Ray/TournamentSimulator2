package clients;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;

@SpringBootApplication
public class ClientApp {

    @Value("${local.server.port}")
    private int port;  
    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientApp.class)
                .profiles("random") 
                .run(args);
    }

    @PostConstruct
    public void init() {
        System.out.println("Client running on random port: " + port);
    }
}