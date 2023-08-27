package ru.tggc.capibaraBotTelegram.capybara;

public class Username {
    private String userID;
    private String peerID;

    private String username;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Username() {

    }

    public Username(String userID, String peerID, String username) {
        this.userID = userID;
        this.peerID = peerID;
        this.username = username;
    }

    @Override
    public String toString() {
        return "Username{" +
                "username='" + username + '\'' +
                "userID='" + userID + '\'' +
                ", peerID='" + peerID + '\'' +
                '}';
    }
}
