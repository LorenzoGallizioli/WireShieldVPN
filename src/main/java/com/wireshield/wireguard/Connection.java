package com.wireshield.wireguard;

import com.wireshield.enums.connectionStates;
public class Connection {
    private connectionStates status;
    private long sentTraffic;
    private long receivedTraffic;
    private long lastHandshakeTime;
    private Peer peer;

    public Connection() {}

    public void updateTraffic(long sentTraffic, long receivedTraffic) {}

    public void updateHandshakeTime(long lastHandshakeTime) {}

    public connectionStates getStatus() {
        return status;
    }

    public long getSentTraffic() {
        return sentTraffic;
    }

    public long getReceivedTraffic() {
        return receivedTraffic;
    }

    public long getLastHandshakeTime() {
        return lastHandshakeTime;
    }

    public String getPeerInfo() {
        return "";
    }

}
