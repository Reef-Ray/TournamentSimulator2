package servers;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

    private final TournamentServer service;

    public TournamentController(TournamentServer service) {
        this.service = service;
    }

    @GetMapping("/tournaments")
    public List<String> getTournaments() {
        return service.getAllTournaments();
    }

    @GetMapping("/available")
    public List<String> getAvailableTournaments() {
        return service.getAvailableTournaments();
    }

    @GetMapping("/info")
    public java.util.Map<String, Integer> info(@RequestParam String name) {
        java.util.Map<String, Integer> map = new java.util.HashMap<>();
        map.put("current", service.getPlayerCount(name));
        map.put("max", service.getMaxPlayers(name));
        return map;
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String tournament,
                           @RequestParam String type,
                           @RequestParam String ip,
                           @RequestParam String port) {

        return service.register(name, tournament, type, ip, port);
    }

    @GetMapping("/observe")
    public java.util.List<String> observe(@RequestParam String name) {
        return service.getObserverMessages(name);
    }
}