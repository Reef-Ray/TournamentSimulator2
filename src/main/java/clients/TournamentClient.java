package clients;

import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TournamentClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String ip;
    private final int port;

    public TournamentClient(Environment env) {
        this.port = Integer.parseInt(env.getProperty("local.server.port"));
        
        // Discover actual local IP address
        String discoveredIP = "localhost";
        try {
            discoveredIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("Failed to discover IP, using localhost: " + e.getMessage());
        }
        this.ip = discoveredIP;
    }

    public void register(String serverUrl, String name, String tournament, String type) {
        String url = serverUrl + "/tournament/register"
                + "?name=" + name
                + "&tournament=" + tournament
                + "&type=" + type
                + "&ip=" + ip
                + "&port=" + port;

        String result = restTemplate.postForObject(url, null, String.class);
        System.out.println("Registration result: " + result);
    }

    public String getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}