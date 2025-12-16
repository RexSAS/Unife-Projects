package it.unife.lp.biblio.view;

import it.unife.lp.biblio.App;
import it.unife.lp.biblio.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class UsersOverviewController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> idColumn;
    @FXML private TableColumn<User, String> nomeColumn;
    @FXML private TableColumn<User, String> cognomeColumn;
    @FXML private TableColumn<User, String> emailColumn;

    private App mainApp;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(c -> c.getValue().idProperty());
        nomeColumn.setCellValueFactory(c -> c.getValue().nomeProperty());
        cognomeColumn.setCellValueFactory(c -> c.getValue().cognomeProperty());
        emailColumn.setCellValueFactory(c -> c.getValue().emailProperty());
    }

    public void setMainApp(App mainApp) {
        this.mainApp = mainApp;
        userTable.setItems(mainApp.getUserData());
    }

    @FXML
    private void handleNewUser() {
        if (mainApp == null) return;

        User u = promptUserData(null);
        if (u == null) return;

        // id unico
        if (existsUserId(u.getId())) {
            alert("ID già esistente", "Esiste già un utente con ID: " + u.getId());
            return;
        }

        mainApp.getUserData().add(u);
        mainApp.saveLibrary();
    }

    @FXML
    private void handleEditUser() {
        if (mainApp == null) return;

        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert("Nessuna selezione", "Seleziona un utente dalla tabella.");
            return;
        }

        User edited = promptUserData(selected);
        if (edited == null) return;

        // se cambia ID, deve restare unico
        String oldId = selected.getId();
        String newId = edited.getId();
        if (!newId.equals(oldId) && existsUserId(newId)) {
            alert("ID già esistente", "Esiste già un utente con ID: " + newId);
            return;
        }

        selected.setId(edited.getId());
        selected.setNome(edited.getNome());
        selected.setCognome(edited.getCognome());
        selected.setEmail(edited.getEmail());

        mainApp.saveLibrary();
    }

    @FXML
    private void handleDeleteUser() {
        if (mainApp == null) return;

        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert("Nessuna selezione", "Seleziona un utente dalla tabella.");
            return;
        }

        // blocca delete se ha prestiti attivi
        boolean hasActiveLoans = mainApp.getLoanData().stream()
                .anyMatch(l -> !l.isRestituito() && l.getUserId().equals(selected.getId()));

        if (hasActiveLoans) {
            alert("Impossibile eliminare", "Questo utente ha prestiti attivi. Restituisci prima i libri.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma eliminazione");
        confirm.setHeaderText("Eliminare l'utente selezionato?");
        confirm.setContentText(selected.getId() + " - " + selected.getNome() + " " + selected.getCognome());
        Optional<ButtonType> res = confirm.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.OK) {
            mainApp.getUserData().remove(selected);
            mainApp.saveLibrary();
        }
    }

    // ---------- helpers ----------

    private boolean existsUserId(String id) {
        return mainApp.getUserData().stream().anyMatch(u -> u.getId().equals(id));
    }

    /**
     * Se base==null -> new
     * Se base!=null -> edit (prefill)
     */
    private User promptUserData(User base) {
        String id = ask("ID utente", base == null ? "" : base.getId());
        if (id == null || id.isBlank()) return null;

        String nome = ask("Nome", base == null ? "" : base.getNome());
        if (nome == null || nome.isBlank()) return null;

        String cognome = ask("Cognome", base == null ? "" : base.getCognome());
        if (cognome == null || cognome.isBlank()) return null;

        String email = ask("Email", base == null ? "" : base.getEmail());
        if (email == null || email.isBlank()) return null;

        return new User(id.trim(), nome.trim(), cognome.trim(), email.trim());
    }

    private String ask(String label, String defaultValue) {
        TextInputDialog d = new TextInputDialog(defaultValue);
        d.setTitle("Utente");
        d.setHeaderText(label);
        d.setContentText(label + ":");
        Optional<String> r = d.showAndWait();
        return r.orElse(null);
    }

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
