package clients;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ClientApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientApp.class)
                .profiles("random")
                .run(args);
    }
}