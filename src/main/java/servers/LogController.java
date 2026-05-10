package servers;
 
import org.springframework.web.bind.annotation.*;
import robots.LoggingBot;
import robots.LogEntry;
import robots.RemoteBot;
import robots.Robot;
import tournaments.Tournament;
 
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
 
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
        
        Set<String> seenMatches = new HashSet<>();
        
        for (Robot r : t.getPlayers()) {
            if (r instanceof LoggingBot) {
                for (LogEntry e : ((LoggingBot) r).getLog()) {
                    String matchKey = e.botName + " vs " + e.opponent;
                    if (!seenMatches.contains(matchKey)) {
                        entries.add(e.toString());
                        seenMatches.add(matchKey);
                    }
                }
            } else if (r instanceof RemoteBot) {

                for (LogEntry e : ((RemoteBot) r).getRemoteLogs()) {
                    String matchKey = e.botName + " vs " + e.opponent;
                    if (!seenMatches.contains(matchKey)) {
                        entries.add(e.toString());
                        seenMatches.add(matchKey);
                    }
                }
            }
        }
        if (entries.isEmpty()) entries.add("No log entries yet.");
        return entries;
    }
}