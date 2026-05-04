package robots;
 
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
 
public class LogEntry {
 
    public final String botName;
    public final String opponent;
    public final String action;
    public final String timestamp;
 
    public LogEntry(String botName, String opponent, String action) {
        this.botName = botName;
        this.opponent = opponent;
        this.action = action;
        this.timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
 
    @Override
    public String toString() {
        return "[" + timestamp + "] " + botName + " -> " + action + " vs " + opponent;
    }
}
 