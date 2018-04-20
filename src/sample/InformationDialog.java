package sample;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class InformationDialog
{
    Alert alert;

    InformationDialog(String headerText, String contentText )
    {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("information.png").toString()));
    }

    public void show()
    {
        alert.showAndWait();
    }
}
