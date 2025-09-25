package ru.tggc.capybaratelegrambot.oldcapybara.DataBase.secret;

@Deprecated
public class NewDataBase {
    private static final String password = "e8b33fc19748490abd992bcf33b78c66c5c7a809f0da94c83ddf4cb149b55bce";
    private static final String urlConnection = "jdbc:postgresql://ec2-52-208-221-89.eu-west-1.compute.amazonaws.com:5432/dfn3l7dpk6iv1p";
    private static final String userDB = "zhzbdlxqllzutx";

    public static String getPassword() {
        return password;
    }

    public static String getUrlConnection() {
        return urlConnection;
    }

    public static String getUserDB() {
        return userDB;
    }
}
