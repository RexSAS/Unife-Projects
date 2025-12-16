package it.unife.lp.biblio.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unife.lp.biblio.model.*;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class JsonStorage {

    private static final Path FILE = Paths.get("library.json");

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static LibraryData load() {
        LibraryData data = new LibraryData();
        data.setBooks(new ArrayList<>());
        data.setUsers(new ArrayList<>());
        data.setLoans(new ArrayList<>());

        if (!Files.exists(FILE)) return data;

        try (Reader reader = Files.newBufferedReader(FILE)) {
            LibraryDataDTO dto = gson.fromJson(reader, LibraryDataDTO.class);
            if (dto == null) return data;

            if (dto.books != null) for (BookDTO b : dto.books) data.getBooks().add(Book.fromDTO(b));
            if (dto.users != null) for (UserDTO u : dto.users) data.getUsers().add(User.fromDTO(u));
            if (dto.loans != null) for (LoanDTO l : dto.loans) data.getLoans().add(Loan.fromDTO(l));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static void save(LibraryData data) {
        try (Writer writer = Files.newBufferedWriter(FILE)) {

            LibraryDataDTO dto = new LibraryDataDTO();

            if (data != null && data.getBooks() != null)
                for (Book b : data.getBooks()) dto.books.add(b.toDTO());

            if (data != null && data.getUsers() != null)
                for (User u : data.getUsers()) dto.users.add(u.toDTO());

            if (data != null && data.getLoans() != null)
                for (Loan l : data.getLoans()) dto.loans.add(l.toDTO());

            gson.toJson(dto, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
