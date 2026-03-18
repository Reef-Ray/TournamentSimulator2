package templates;

import java.util.List;
import robots.*;

public record RemoteInfo(String opponentName, List<History> history) {}