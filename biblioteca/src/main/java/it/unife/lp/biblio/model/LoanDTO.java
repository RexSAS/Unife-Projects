package it.unife.lp.biblio.model;

public class LoanDTO {
    public String id;
    public String userId;
    public String bookIsbn;
    public String startDate;
    public String dueDate;
    public boolean restituito;

    public LoanDTO() {}
    public LoanDTO(String id, String userId, String bookIsbn, String startDate, String dueDate, boolean restituito) {
        this.id = id; this.userId = userId; this.bookIsbn = bookIsbn;
        this.startDate = startDate; this.dueDate = dueDate; this.restituito = restituito;
    }
}
