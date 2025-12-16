package it.unife.lp.biblio.view;

import it.unife.lp.biblio.App;
import it.unife.lp.biblio.model.Book;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;

import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import java.io.IOException;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class BookOverviewController {

    @FXML
    private TableView<Book> bookTable;

    @FXML
    private TableColumn<Book, String> isbnColumn;
    @FXML
    private TableColumn<Book, String> titoloColumn;
    @FXML
    private TableColumn<Book, String> autoreColumn;
    @FXML
    private TableColumn<Book, Number> annoColumn;
    @FXML
    private TableColumn<Book, String> genereColumn;
    @FXML
    private TableColumn<Book, Boolean> disponibileColumn;
    @FXML
    private Label isbnLabel;
    @FXML
    private Label titoloLabel;
    @FXML
    private Label autoreLabel;
    @FXML
    private Label annoLabel;
    @FXML
    private Label genereLabel;
    @FXML
    private Label disponibileLabel;
    @FXML
    private TextField searchField;
    @FXML
    private ChoiceBox<String> availabilityChoiceBox;
    @FXML
    private Button loanActionButton;

    private App mainApp;
    private FilteredList<Book> filteredBooks;

    @FXML
    private void handleDeleteBook() {
        int selectedIndex = bookTable.getSelectionModel().getSelectedIndex();
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedIndex >= 0) {
            if (!mainApp.isBookAvailable(selectedBook.getIsbn())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Libro in prestito");
                alert.setHeaderText("Questo libro è in prestito");
                alert.showAndWait();
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma eliminazione");
            alert.setHeaderText("Eliminare il libro selezionato?");
            alert.setContentText("Questa operazione non può essere annullata.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK && selectedBook != null) {
                mainApp.getBookData().remove(selectedBook);
                mainApp.saveLibrary();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Nessuna selezione");
            alert.setHeaderText("Nessun libro selezionato");
            alert.setContentText("Seleziona un libro nella tabella.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleOpenUsers() {
        if (mainApp != null)
            mainApp.showUsersWindow();
    }

    @FXML
    private void handleNewBook() {
        Book temp = new Book("", "", "", 0, "");
        boolean ok = showBookEditDialog(temp);

        if (ok) {
            boolean exists = bookTable.getItems().stream().anyMatch(b -> b.getIsbn().equals(temp.getIsbn()));

            if (exists) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("ISBN duplicato");
                alert.setHeaderText("ISBN già presente");
                alert.setContentText("Esiste già un libro con questo ISBN.");
                alert.showAndWait();
                return;
            }

            mainApp.getBookData().add(temp);
            mainApp.saveLibrary();
        }
    }

    @FXML
    private void handleEditBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        String oldIsbn = selected.getIsbn();

        boolean ok = showBookEditDialog(selected);

        if (ok) {
            if (!selected.getIsbn().equals(oldIsbn)) {
                boolean exists = bookTable.getItems().stream()
                        .anyMatch(b -> b != selected && b.getIsbn().equals(selected.getIsbn()));

                if (exists) {
                    selected.setIsbn(oldIsbn);

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ISBN duplicato");
                    alert.setHeaderText("ISBN già presente");
                    alert.setContentText("Esiste già un libro con questo ISBN.");
                    alert.showAndWait();
                }
            }

            mainApp.saveLibrary();
        }
    }

    private void applyFilters() {
        if (mainApp == null || filteredBooks == null)
            return;
        String raw = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
        String[] tokens = raw.split("\\s*,\\s*");

        String availability = availabilityChoiceBox.getValue();

        filteredBooks.setPredicate(book -> {

            // filtro disponibilità
            if ("Disponibili".equals(availability) && !mainApp.isBookAvailable(book.getIsbn()))
                return false;

            if ("In prestito".equals(availability) && mainApp.isBookAvailable(book.getIsbn()))
                return false;

            // filtro testo
            if (raw.isBlank())
                return true;

            String isbn = book.getIsbn().toLowerCase();
            String titolo = book.getTitolo().toLowerCase();
            String autore = book.getAutore().toLowerCase();
            String genere = book.getGenere().toLowerCase();

            for (String token : tokens) {
                if (token.isBlank())
                    continue;

                boolean match = isbn.contains(token) || titolo.contains(token) || autore.contains(token)
                        || genere.contains(token);

                if (!match)
                    return false;
            }
            return true;
        });
    }

    private void updateLoanButton(Book book) {
        if (loanActionButton == null)
            return;

        if (book == null) {
            loanActionButton.setVisible(false);
            loanActionButton.setManaged(false);
            return;
        }

        loanActionButton.setVisible(true);
        loanActionButton.setManaged(true);

        if (mainApp.isBookAvailable(book.getIsbn())) {
            loanActionButton.setText("Prestito");
            loanActionButton.setDisable(false);
        } else {
            loanActionButton.setText("Restituisci");
            loanActionButton.setDisable(false);
        }
    }

    @FXML
    private void handleLoanAction() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        if (mainApp.isBookAvailable(selected.getIsbn())) {
            handleBorrow(selected);
        } else {
            handleReturn(selected);
        }
    }

    private void handleBorrow(Book book) {
        // 1) chiedi userId
        TextInputDialog d1 = new TextInputDialog();
        d1.setTitle("Prestito");
        d1.setHeaderText("Inserisci User ID");
        d1.setContentText("User ID:");
        var r1 = d1.showAndWait();
        if (r1.isEmpty() || r1.get().isBlank())
            return;
        String userId = r1.get().trim();

        // 2) controlla che l’utente esista (se hai già users in mainApp)
        boolean userExists = mainApp.getUserData().stream().anyMatch(u -> u.getId().equals(userId));
        if (!userExists) {
            new Alert(Alert.AlertType.ERROR, "User ID non trovato.").showAndWait();
            return;
        }

        // 3) chiedi scadenza (ISO)
        TextInputDialog d2 = new TextInputDialog();
        d2.setTitle("Prestito");
        d2.setHeaderText("Inserisci data scadenza (YYYY-MM-DD)");
        d2.setContentText("Due date:");
        var r2 = d2.showAndWait();
        if (r2.isEmpty() || r2.get().isBlank())
            return;
        String dueDate = r2.get().trim();

        // check format minimo
        if (!dueDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            new Alert(Alert.AlertType.ERROR, "Formato data non valido. Usa YYYY-MM-DD").showAndWait();
            return;
        }

        String startDate = java.time.LocalDate.now().toString();
        boolean ok = mainApp.borrowBook(book, userId, startDate, dueDate);

        if (!ok) {
            new Alert(Alert.AlertType.ERROR, "Prestito non riuscito (libro non disponibile o già in prestito).")
                    .showAndWait();
        } else {
            bookTable.refresh();
            applyFilters();
            updateLoanButton(book);
            showBookDetails(book);
        }
    }

    private void handleReturn(Book book) {
        var loanOpt = mainApp.getLoanData().stream()
                .filter(l -> !l.isRestituito() && l.getBookIsbn().equals(book.getIsbn()))
                .findFirst();

        if (loanOpt.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Prestito attivo non trovato per questo libro.").showAndWait();
            return;
        }

        boolean ok = mainApp.returnLoan(loanOpt.get());
        if (!ok) {
            new Alert(Alert.AlertType.ERROR, "Restituzione non riuscita.").showAndWait();
        } else {
            bookTable.refresh();
            applyFilters();
            updateLoanButton(book);
            showBookDetails(book);
        }
    }

    @FXML
    private void initialize() {

        // --- TABELLA ---

        isbnColumn.setCellValueFactory(cell -> cell.getValue().isbnProperty());
        titoloColumn.setCellValueFactory(cell -> cell.getValue().titoloProperty());
        autoreColumn.setCellValueFactory(cell -> cell.getValue().autoreProperty());
        annoColumn.setCellValueFactory(cell -> cell.getValue().annoProperty());
        genereColumn.setCellValueFactory(cell -> cell.getValue().genereProperty());
        disponibileColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleBooleanProperty(
                mainApp != null && mainApp.isBookAvailable(cell.getValue().getIsbn())));

        disponibileColumn.setCellFactory(col -> new TableCell<Book, Boolean>() {
            @Override
            protected void updateItem(Boolean value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(value ? "Sì" : "No");
                }
            }
        });

        // --- DISPONIBILITA' ---

        availabilityChoiceBox.getItems().addAll(
                "Tutti",
                "Disponibili",
                "In prestito");
        availabilityChoiceBox.setValue("Tutti");

        searchField.textProperty().addListener((o, a, b) -> applyFilters());
        availabilityChoiceBox.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> applyFilters());
        // --- BUTTONS ---

        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            showBookDetails(newSel);
            updateLoanButton(newSel);
        });
        updateLoanButton(null);

        // --- SEARCH BAR ---

        searchField.setTooltip(new Tooltip(
                "Puoi inserire più filtri separati da virgola.\n" +
                        "Esempio: Stephen King, Horror\n" +
                        "La ricerca è parziale e su tutti i campi."));

    }

    public void setMainApp(App mainApp) {
        this.mainApp = mainApp;

        filteredBooks = new FilteredList<>(mainApp.getBookData(), b -> true);

        SortedList<Book> sortedBooks = new SortedList<>(filteredBooks);
        sortedBooks.comparatorProperty().bind(bookTable.comparatorProperty());
        bookTable.setItems(sortedBooks);

        applyFilters();
        bookTable.refresh();

        mainApp.getLoanData().addListener((javafx.collections.ListChangeListener.Change<?> c) -> {
            applyFilters();
            bookTable.refresh();
        });
    }

    private boolean showBookEditDialog(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unife/lp/biblio/BookEditDialog.fxml"));
            DialogPane dialogPane = loader.load();

            BookEditDialogController controller = loader.getController();
            controller.setBook(book);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Libro");
            dialog.setDialogPane(dialogPane);
            dialog.initOwner(bookTable.getScene().getWindow());

            dialog.showAndWait();
            return controller.isOkClicked();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showBookDetails(Book book) {
        if (book == null) {
            isbnLabel.setText("");
            titoloLabel.setText("");
            autoreLabel.setText("");
            annoLabel.setText("");
            genereLabel.setText("");
            disponibileLabel.setText("");
            return;
        }

        isbnLabel.setText(book.getIsbn());
        titoloLabel.setText(book.getTitolo());
        autoreLabel.setText(book.getAutore());
        annoLabel.setText(Integer.toString(book.getAnno()));
        genereLabel.setText(book.getGenere());

        if (mainApp.isBookAvailable(book.getIsbn())) {
            disponibileLabel.setText("Disponibile");
            return;
        }

        var loanOpt = mainApp.getLoanData().stream()
                .filter(l -> !l.isRestituito() && l.getBookIsbn().equals(book.getIsbn()))
                .findFirst();

        if (loanOpt.isEmpty()) {
            disponibileLabel.setText("In prestito");
            return;
        }

        var loan = loanOpt.get();

        var userOpt = mainApp.getUserData().stream()
                .filter(u -> u.getId().equals(loan.getUserId()))
                .findFirst();

        if (userOpt.isPresent()) {
            var u = userOpt.get();
            disponibileLabel.setText(
                    u.getNome() + " " + u.getCognome() + " " +
                            loan.getDueDate());
        } else {
            disponibileLabel.setText(
                    loan.getDueDate());
        }
    }

}
