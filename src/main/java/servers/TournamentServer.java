package servers;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.springframework.stereotype.Service;

import robots.*;
import tournaments.*;

@Service
public class TournamentServer {

  private final Map<String, Tournament> tournaments = new HashMap<>();
  private final ExecutorService executorService = Executors.newFixedThreadPool(4);

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

    public int getPlayerCount(String name) {
        Tournament t = tournaments.get(name);
        if (t == null) return 0;
        return t.getPlayers() == null ? 0 : t.getPlayers().size();
    }

    public int getMaxPlayers(String name) {
        Tournament t = tournaments.get(name);
        if (t == null) return 0;
        return t.getMaxPlayers();
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
            // Run tournament asynchronously to avoid blocking the HTTP request handler
            executorService.submit(() -> {
                try {
                    t.run();
                } catch (Exception e) {
                    System.err.println("Error running tournament " + name + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }

    public java.util.List<String> getObserverMessages(String tournamentName) {
        Tournament t = tournaments.get(tournamentName);
        java.util.List<String> messages = new java.util.ArrayList<>();
        if (t == null) return messages;
        java.util.Set<String> seen = new java.util.LinkedHashSet<>();
        
        for (Robot r : t.getPlayers()) {
            for (History h : r.getHistory()) {
                if (h == null) continue;
                String p1 = h.player1 == null ? "" : h.player1;
                String p2 = h.player2 == null ? "" : h.player2;
                String a = p1.compareTo(p2) <= 0 ? p1 : p2;
                String b = p1.compareTo(p2) <= 0 ? p2 : p1;
                String moveA, moveB;
                int scoreA = 0, scoreB = 0;
                if (a.equals(p1)) {
                    moveA = h.player1Move == null ? "" : h.player1Move;
                    moveB = h.player2Move == null ? "" : h.player2Move;
                    if (h.outcome != null && h.outcome.length >= 2) {
                        scoreA = h.outcome[0];
                        scoreB = h.outcome[1];
                    }
                } else {
                    moveA = h.player2Move == null ? "" : h.player2Move;
                    moveB = h.player1Move == null ? "" : h.player1Move;
                    if (h.outcome != null && h.outcome.length >= 2) {
                        scoreA = h.outcome[1];
                        scoreB = h.outcome[0];
                    }
                }

                String key = a + "|" + b + "|" + moveA + "|" + moveB + "|" + scoreA + "," + scoreB;
                if (seen.contains(key)) continue;
                seen.add(key);

                String movesLine = String.format("%s chose %s | %s chose %s", a, moveA, b, moveB);
                String scoreLine = String.format("%s=%d | %s=%d", a, scoreA, b, scoreB);
                messages.add(movesLine);
                messages.add(scoreLine);
            }
        }
        if (messages.isEmpty()) messages.add("No observer messages yet.");
        return messages;
    }
}