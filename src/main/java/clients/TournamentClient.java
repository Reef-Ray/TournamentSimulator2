package clients;

import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

public class TournamentClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String ip = "localhost";
    private final int port;

    public TournamentClient(Environment env) {
        this.port = Integer.parseInt(env.getProperty("local.server.port"));
    }

    public void register(String serverUrl, String name, String tournament, String type) {
        String url = serverUrl + "/tournament/register"
                + "?name=" + name
                + "&tournament=" + tournament
                + "&type=" + type
                + "&ip=" + ip
                + "&port=" + port;

        restTemplate.postForObject(url, null, String.class);
    }
}