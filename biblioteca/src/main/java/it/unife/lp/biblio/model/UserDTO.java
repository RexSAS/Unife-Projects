package it.unife.lp.biblio.model;

public class UserDTO {
    public String id;
    public String nome;
    public String cognome;
    public String email;

    public UserDTO() {}
    public UserDTO(String id, String nome, String cognome, String email) {
        this.id = id; this.nome = nome; this.cognome = cognome; this.email = email;
    }
}
