package it.unife.lp.biblio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import it.unife.lp.biblio.model.Book;
import it.unife.lp.biblio.model.Loan;
import it.unife.lp.biblio.model.User;
import it.unife.lp.biblio.persistence.JsonStorage;
import it.unife.lp.biblio.persistence.LibraryData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import javafx.stage.Modality;

public class App extends Application {

    private static Scene scene;
    private LibraryData libraryData;

    private final ObservableList<Book> bookData = FXCollections.observableArrayList();

    public ObservableList<Book> getBookData() {
        return bookData;
    }

    private final ObservableList<User> userData = FXCollections.observableArrayList();
    private final ObservableList<Loan> loanData = FXCollections.observableArrayList();

    public ObservableList<User> getUserData() {
        return userData;
    }

    public ObservableList<Loan> getLoanData() {
        return loanData;
    }

    public boolean isBookAvailable(String isbn) {
        if (isbn == null)
            return false;

        return loanData.stream()
                .noneMatch(l -> !l.isRestituito() && isbn.equals(l.getBookIsbn()));
    }

    public boolean borrowBook(Book book,
            String userId,
            String startDateIso,
            String dueDateIso) {

        if (book == null || userId == null || userId.isBlank())
            return false;

        // controlla disponibilità derivata dai prestiti
        if (!isBookAvailable(book.getIsbn()))
            return false;

        String loanId = "L-" + System.currentTimeMillis();
        Loan loan = new Loan(loanId, userId, book.getIsbn(), startDateIso, dueDateIso, false);

        loanData.add(loan);
        saveLibrary();
        return true;
    }

    public boolean returnLoan(Loan loan) {
        if (loan == null)
            return false;
        if (loan.isRestituito())
            return false;

        loan.setRestituito(true);
        saveLibrary();
        return true;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("primary.fxml"));
        Parent root = loader.load();

        libraryData = JsonStorage.load();
        bookData.addAll(libraryData.getBooks());
        userData.addAll(libraryData.getUsers());
        loanData.addAll(libraryData.getLoans());

        it.unife.lp.biblio.view.BookOverviewController controller = loader.getController();
        controller.setMainApp(this);

        scene = new Scene(root, 1280, 720);
        stage.setTitle("Biblioteca - Libri");
        stage.setScene(scene);
        stage.show();
    }

    public void saveLibrary() {
        libraryData.setBooks(new ArrayList<>(bookData));
        libraryData.setUsers(new ArrayList<>(userData));
        libraryData.setLoans(new ArrayList<>(loanData));
        JsonStorage.save(libraryData);
    }

    public void showUsersWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("UsersOverview.fxml"));
            Parent root = loader.load();

            it.unife.lp.biblio.view.UsersOverviewController controller = loader.getController();
            controller.setMainApp(this);

            Stage stage = new Stage();
            stage.setTitle("Biblioteca - Utenti");
            stage.initModality(Modality.NONE);
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}