package clients;

import org.springframework.web.bind.annotation.*;

@RestController
public class ClientChanges {

    @GetMapping("/action")
    public String getAction() {
        return "Cooperate"; // user edits this
    }
}