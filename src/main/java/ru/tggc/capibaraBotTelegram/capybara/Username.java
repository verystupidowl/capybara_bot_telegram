package ru.tggc.capibaraBotTelegram.capybara;

public class Username {
    private String userID;
    private String peerID;

    public String getPeerID() {
        return peerID;
    }

    public void setPeerID(String peerID) {
        this.peerID = peerID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Username() {

    }

    public Username(String userID, String peerID) {
        this.userID = userID;
        this.peerID = peerID;
    }

    @Override
    public String toString() {
        return "Username{" +
                "userID='" + userID + '\'' +
                ", peerID='" + peerID + '\'' +
                '}';
    }
}
