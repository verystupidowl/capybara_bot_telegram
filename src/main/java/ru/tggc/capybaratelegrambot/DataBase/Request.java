package ru.tggc.capybaratelegrambot.DataBase;


import ru.tggc.capybaratelegrambot.capybara.Capybara;

@Deprecated(forRemoval = true)
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
