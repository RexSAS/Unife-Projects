package it.unife.lp.biblio.model;

import javafx.beans.property.*;

public class Loan {
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty userId = new SimpleStringProperty();
    private final StringProperty bookIsbn = new SimpleStringProperty();
    private final StringProperty startDate = new SimpleStringProperty(); // "2025-12-16"
    private final StringProperty dueDate = new SimpleStringProperty();   // "2025-12-30"
    private final BooleanProperty restituito = new SimpleBooleanProperty(false);

    public Loan() {}

    public Loan(String id, String userId, String bookIsbn, String startDate, String dueDate, boolean restituito) {
        this.id.set(id);
        this.userId.set(userId);
        this.bookIsbn.set(bookIsbn);
        this.startDate.set(startDate);
        this.dueDate.set(dueDate);
        this.restituito.set(restituito);
    }

    public String getId() { return id.get(); }
    public void setId(String v) { id.set(v); }
    public StringProperty idProperty() { return id; }

    public String getUserId() { return userId.get(); }
    public void setUserId(String v) { userId.set(v); }
    public StringProperty userIdProperty() { return userId; }

    public String getBookIsbn() { return bookIsbn.get(); }
    public void setBookIsbn(String v) { bookIsbn.set(v); }
    public StringProperty bookIsbnProperty() { return bookIsbn; }

    public String getStartDate() { return startDate.get(); }
    public void setStartDate(String v) { startDate.set(v); }
    public StringProperty startDateProperty() { return startDate; }

    public String getDueDate() { return dueDate.get(); }
    public void setDueDate(String v) { dueDate.set(v); }
    public StringProperty dueDateProperty() { return dueDate; }

    public boolean isRestituito() { return restituito.get(); }
    public void setRestituito(boolean v) { restituito.set(v); }
    public BooleanProperty restituitoProperty() { return restituito; }

    // DTO helpers
    public LoanDTO toDTO() {
        return new LoanDTO(getId(), getUserId(), getBookIsbn(), getStartDate(), getDueDate(), isRestituito());
    }
    public static Loan fromDTO(LoanDTO d) {
        return new Loan(d.id, d.userId, d.bookIsbn, d.startDate, d.dueDate, d.restituito);
    }
}
