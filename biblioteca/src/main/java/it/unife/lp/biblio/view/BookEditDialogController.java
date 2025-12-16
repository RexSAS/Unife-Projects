package it.unife.lp.biblio.view;

import java.util.function.UnaryOperator;

import it.unife.lp.biblio.model.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;


public class BookEditDialogController {

    @FXML private DialogPane dialogPane;
    @FXML private TextField isbnField;
    @FXML private TextField titoloField;
    @FXML private TextField autoreField;
    @FXML private TextField annoField;
    @FXML private TextField genereField;
    

    private Book book;
    private boolean okClicked = false;

    @FXML
    private void initialize() {

        UnaryOperator<TextFormatter.Change> isbnFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,13}")) return change;
            return null;
        };
        isbnField.setTextFormatter(new TextFormatter<>(isbnFilter));

        UnaryOperator<TextFormatter.Change> annoFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,4}")) return change;
            return null;
        };
        annoField.setTextFormatter(new TextFormatter<>(annoFilter));


        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);

        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!isInputValid()) {
                event.consume();
                return;
            }

            book.setIsbn(isbnField.getText().trim());
            book.setTitolo(titoloField.getText().trim());
            book.setAutore(autoreField.getText().trim());
            book.setAnno(Integer.parseInt(annoField.getText().trim()));
            book.setGenere(genereField.getText().trim());

            okClicked = true;
        });
    }

    private boolean isInputValid() {
        StringBuilder error = new StringBuilder();

        String isbn = isbnField.getText().trim();
        if (!isbn.matches("\\d{13}")) {
            error.append("ISBN non valido (13 cifre).\n");
        }

        String annoTxt = annoField.getText().trim();
        if (annoTxt.isEmpty()) {
            error.append("Anno non valido.\n");
        } else {
            int a = Integer.parseInt(annoTxt);
            if (a < 1400 || a > 2100) error.append("Anno fuori range (1400-2100).\n");
        }


        if (isbnField.getText() == null || isbnField.getText().trim().isEmpty()) error.append("ISBN non valido.\n");
        if (titoloField.getText() == null || titoloField.getText().trim().isEmpty()) error.append("Titolo non valido.\n");
        if (autoreField.getText() == null || autoreField.getText().trim().isEmpty()) error.append("Autore non valido.\n");
        if (genereField.getText() == null || genereField.getText().trim().isEmpty()) error.append("Genere non valido.\n");

        try {
            Integer.parseInt(annoField.getText().trim());
        } catch (Exception e) {
            error.append("Anno non valido (deve essere un numero).\n");
        }

        if (error.length() == 0) return true;

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Campi non validi");
        alert.setHeaderText("Correggi i campi evidenziati");
        alert.setContentText(error.toString());
        alert.showAndWait();
        return false;
    }

    public void setBook(Book book){
        this.book = book;

        isbnField.setText(book.getIsbn());
        titoloField.setText(book.getTitolo());
        autoreField.setText(book.getAutore());
        annoField.setText(Integer.toString(book.getAnno()));
        genereField.setText(book.getGenere());
    }

    public boolean isOkClicked(){
        return okClicked;
    }


}
