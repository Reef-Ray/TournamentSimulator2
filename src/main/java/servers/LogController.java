package servers;
 
import org.springframework.web.bind.annotation.*;
import robots.LoggingBot;
import robots.LogEntry;
import robots.Robot;
import tournaments.Tournament;
 
import java.util.ArrayList;
import java.util.List;
 
@RestController
@RequestMapping("/tournament")
public class LogController {
 
    private final TournamentServer service;
 
    public LogController(TournamentServer service) {
        this.service = service;
    }
 
    @GetMapping("/log")
    public List<String> getLog(@RequestParam String name) {
        Tournament t = service.getTournament(name);
        List<String> entries = new ArrayList<>();
        if (t == null) return entries;
        for (Robot r : t.getPlayers()) {
            if (r instanceof LoggingBot) {
                for (LogEntry e : ((LoggingBot) r).getLog()) {
                    entries.add(e.toString());
                }
            }
        }
        if (entries.isEmpty()) entries.add("No log entries yet.");
        return entries;
    }
}