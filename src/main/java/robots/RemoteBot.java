package robots;

import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import templates.RemoteInfo;
import java.util.ArrayList;
import java.util.List;

public class RemoteBot extends Robot {

    private final RestClient client;
    private final String ip;
    private final String port;
    private final List<LogEntry> remoteLogs = new ArrayList<>();

    public RemoteBot(String name, String ip, String port) {
        super(name);
        this.ip = ip;
        this.port = port;
        this.client = RestClient.create();
    }

    @Override
    public String getAction(String opponentName) {
        String uri = "http://" + this.ip + ":" + this.port + "/action";
        RemoteInfo info = new RemoteInfo(opponentName, getHistory(), new ArrayList<>());
        

        try {
            RemoteInfo response = this.client.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(info) 
                    .retrieve()
                    .body(RemoteInfo.class);
            
            // Store log entries received from remote client
            if (response != null && response.logEntries() != null) {
                remoteLogs.addAll(response.logEntries());
            }
            
            // Extract action from the response - expecting first element
            if (response != null && response.opponentName() != null && !response.opponentName().isEmpty()) {
                return response.opponentName();
            }
            return "Error";
        } catch (Exception e) {
            System.err.println("Communication failure with bot " + getName() + " at " + uri);
            return "Error"; // Default action on failure
        }
    }

    public List<LogEntry> getRemoteLogs() {
        return remoteLogs;
    }
}