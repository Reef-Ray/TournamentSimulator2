package clients;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import robots.*;
import templates.RemoteInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@RestController
public class ClientApp extends SpringBootServletInitializer {

    private Robot bot = new RandomBot("ClientBot");
    private int assignedPort;
    private String assignedIP = "localhost";

    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientApp.class)
                .profiles("random")
                .run(args);
    }

    @Bean
    public ApplicationListener<ServletWebServerInitializedEvent> serverPortListenerBean() {
        return event -> {
            this.assignedPort = event.getWebServer().getPort();
            System.out.println("Client listening on port: " + this.assignedPort);
            
            try {
                this.assignedIP = InetAddress.getLocalHost().getHostAddress();
                System.out.println("Client IP: " + this.assignedIP);
            } catch (UnknownHostException e) {
                System.err.println("Failed to get local IP address: " + e.getMessage());
            }
        };
    }

    public int getAssignedPort() {
        return assignedPort;
    }

    public String getAssignedIP() {
        return assignedIP;
    }

    @PostMapping("/action")
    @ResponseStatus(HttpStatus.OK)
    public String getAction(@RequestBody RemoteInfo details) {
        return this.bot.getAction(details.opponentName());
    }
}