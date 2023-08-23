package ru.tggc.capibaraBotTelegram.DataBase;


import ru.tggc.capibaraBotTelegram.capybara.Capybara;

public class Request {
    private Capybara capybara;

    public Capybara getCapybara() {
        return capybara;
    }

    public void setCapybara(Capybara capybara) {
        this.capybara = capybara;
    }

    public Request(Capybara capybara, String textRequest) {
        this.capybara = capybara;
    }
}
