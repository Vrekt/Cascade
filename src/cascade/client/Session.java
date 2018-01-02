package cascade.client;

public class Session {

    private final String username;
    private final int uniqueId;

    private long lastPing;

    public Session(String username, int uniqueId) {
        this.username = username;
        this.uniqueId = uniqueId;

        lastPing = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public long getLastPingTime() {
        return lastPing;
    }

    public void setLastPingTime(long lastPing) {
        this.lastPing = lastPing;
    }
}
