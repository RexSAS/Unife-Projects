package it.unife.lp.biblio.model;

import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Book {

    private final StringProperty isbn = new SimpleStringProperty();
    private final StringProperty titolo = new SimpleStringProperty();
    private final StringProperty autore = new SimpleStringProperty();
    private final IntegerProperty anno = new SimpleIntegerProperty();
    private final StringProperty genere = new SimpleStringProperty();

    public Book(){}

    public Book(String isbn, String titolo, String autore, int anno, String genere){
        this.isbn.set(isbn);
        this.titolo.set(titolo);
        this.autore.set(autore);
        this.anno.set(anno);
        this.genere.set(genere);
    }

    public String getIsbn(){return isbn.get();}
    public void setIsbn(String value){isbn.set(value);}
    public StringProperty isbnProperty(){return isbn;}

    public String getTitolo(){return titolo.get();}
    public void setTitolo(String value){titolo.set(value);}
    public StringProperty titoloProperty(){return titolo;}

    public String getAutore(){return autore.get();}
    public void setAutore(String value){autore.set(value);}
    public StringProperty autoreProperty(){return autore;}

    public int getAnno(){return anno.get();}
    public void setAnno(int value){anno.set(value);}
    public IntegerProperty annoProperty(){return anno;}

    public String getGenere(){return genere.get();}
    public void setGenere(String value){genere.set(value);}
    public StringProperty genereProperty(){return genere;}

    public BookDTO toDTO() {
        return new BookDTO(getIsbn(), getTitolo(), getAutore(), getAnno(), getGenere());
    }

    public static Book fromDTO(BookDTO d) {
        return new Book(d.isbn, d.titolo, d.autore, d.anno, d.genere);
    }
}
