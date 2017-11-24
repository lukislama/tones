package sample;

import javafx.scene.control.Alert;

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
        alert.showAndWait();
    }
}
