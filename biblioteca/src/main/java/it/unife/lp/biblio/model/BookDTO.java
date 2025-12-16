package it.unife.lp.biblio.model;

public class BookDTO {
    public String isbn;
    public String titolo;
    public String autore;
    public int anno;
    public String genere;
    public boolean disponibile;

    public BookDTO() {}

    public BookDTO(String isbn, String titolo, String autore, int anno, String genere) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.autore = autore;
        this.anno = anno;
        this.genere = genere;
    }
}
