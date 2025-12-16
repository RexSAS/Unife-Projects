package it.unife.lp.biblio.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty nome = new SimpleStringProperty();
    private final StringProperty cognome = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();

    public User() {}

    public User(String id, String nome, String cognome, String email) {
        this.id.set(id);
        this.nome.set(nome);
        this.cognome.set(cognome);
        this.email.set(email);
    }

    public String getId() { return id.get(); }
    public void setId(String v) { id.set(v); }
    public StringProperty idProperty() { return id; }

    public String getNome() { return nome.get(); }
    public void setNome(String v) { nome.set(v); }
    public StringProperty nomeProperty() { return nome; }

    public String getCognome() { return cognome.get(); }
    public void setCognome(String v) { cognome.set(v); }
    public StringProperty cognomeProperty() { return cognome; }

    public String getEmail() { return email.get(); }
    public void setEmail(String v) { email.set(v); }
    public StringProperty emailProperty() { return email; }

    // DTO helpers
    public UserDTO toDTO() {
        return new UserDTO(getId(), getNome(), getCognome(), getEmail());
    }
    public static User fromDTO(UserDTO d) {
        return new User(d.id, d.nome, d.cognome, d.email);
    }
}
