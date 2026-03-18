package servers;

import org.springframework.web.bind.annotation.*;
import robots.*;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

    private final TournamentServer service;

    public TournamentController(TournamentServer service) {
        this.service = service;
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String tournament,
                           @RequestParam String type,
                           @RequestParam String ip,
                           @RequestParam String port) {

        return service.register(name, tournament, type, ip, port);
    }
}