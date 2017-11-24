package sample;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class WarningDialog
{
    String headerText, contentText;
    WarningDialog(String headerText, String contentText)
    {
        this.headerText = headerText;
        this.contentText = contentText;
    }

    public void show()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Chyba");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("warning-sign.png").toString()));
        alert.showAndWait();
    }
}
