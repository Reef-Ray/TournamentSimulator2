package robots;
 
import java.util.ArrayList;
import java.util.List;
 
public class LoggingBot extends Robot {
 
    private final Robot wrapped;
    private final List<LogEntry> log = new ArrayList<>();
 
    public LoggingBot(Robot wrapped) {
        super(wrapped.getName());
        this.wrapped = wrapped;
    }
 
    @Override
    public String getAction(String enemyName) {
        String action = wrapped.getAction(enemyName);
        log.add(new LogEntry(wrapped.getName(), enemyName, action));
        return action;
    }
 
    @Override
    public int getScore() {
        return wrapped.getScore();
    }
 
    @Override
    public List<History> getHistory() {
        return wrapped.getHistory();
    }
 
    @Override
    public void addHistory(History h) {
        wrapped.addHistory(h);
    }
 
    public List<LogEntry> getLog() {
        return log;
    }
}