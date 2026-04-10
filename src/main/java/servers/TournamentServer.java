package servers;

import java.util.*;

import org.springframework.stereotype.Service;

import robots.*;
import tournaments.*;

@Service
public class TournamentServer {

  private final Map<String, Tournament> tournaments = new HashMap<>();

    public void addTournament(String name, Tournament t) {
        tournaments.put(name, t);
    }

    public String register(String name, String tournamentName, String type, String ip, String port) {
        Tournament t = tournaments.get(tournamentName);

        if (t == null) return "Tournament not found";
        if (t.checkEnd()) return "Tournament closed";

        Robot r;
        if ("remote".equalsIgnoreCase(type)) {
            r = new RemoteBot(name, ip, port);
        } else if ("human".equalsIgnoreCase(type)) {
            r = new HumanBot(name);
        } else {
            r = new DefectBot(name); // fallback
        }

        t.getPlayers().add(r);
        return "Registered";
    }

    public Tournament getTournament(String name) {
        return tournaments.get(name);
    }

    public List<String> getAllTournaments() {
        List<String> all = new ArrayList<>();
        for (Tournament t : tournaments.values()) {
            all.add(t.getName());
        }
        return all;
    }

    public List<String> getAvailableTournaments() {
        List<String> available = new ArrayList<>();
        for (Tournament t : tournaments.values()) {
            if (t.isOpen()) {
                available.add(t.getName());
            }
        }
        return available;
    }

    public void runTournament(String name) {
        Tournament t = tournaments.get(name);
        if (t != null && !t.checkEnd()) {
            t.run();
        }
    }
}