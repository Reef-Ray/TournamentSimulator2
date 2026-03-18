package robots;

import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import templates.RemoteInfo;

public class RemoteBot extends Robot {

    private final RestClient client;
    private final String ip;
    private final String port;

    public RemoteBot(String name, String ip, String port) {
        super(name);
        this.ip = ip;
        this.port = port;
        this.client = RestClient.create();
    }

    @Override
    public String getAction(String opponentName) {
        String uri = "http://" + this.ip + ":" + this.port + "/action";
        RemoteInfo info = new RemoteInfo(opponentName, getHistory());
        

        try {
            return this.client.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(info) 
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            System.err.println("Communication failure with bot " + getName() + " at " + uri);
            return null; 
        }
    }
}