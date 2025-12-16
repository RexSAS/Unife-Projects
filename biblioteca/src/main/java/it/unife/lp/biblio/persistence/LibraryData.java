package it.unife.lp.biblio.persistence;

import java.util.List;
import it.unife.lp.biblio.model.Book;
import it.unife.lp.biblio.model.User;
import it.unife.lp.biblio.model.Loan;

public class LibraryData {
    private List<Book> books;
    private List<User> users;
    private List<Loan> loans;

    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public List<Loan> getLoans() { return loans; }
    public void setLoans(List<Loan> loans) { this.loans = loans; }
}
