package it.unife.lp.biblio.persistence;

import it.unife.lp.biblio.model.BookDTO;
import it.unife.lp.biblio.model.UserDTO;
import it.unife.lp.biblio.model.LoanDTO;

import java.util.ArrayList;
import java.util.List;

public class LibraryDataDTO {
    public List<BookDTO> books = new ArrayList<>();
    public List<UserDTO> users = new ArrayList<>();
    public List<LoanDTO> loans = new ArrayList<>();

    public LibraryDataDTO() {}
}
