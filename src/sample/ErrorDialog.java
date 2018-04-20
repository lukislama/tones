package sample;

        import javafx.scene.control.Alert;
        import javafx.scene.image.Image;
        import javafx.stage.Stage;

public class ErrorDialog
{
    Alert alert;

    ErrorDialog(String headerText, String contentText )
    {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("error.png").toString()));
    }

    public void show()
    {
        alert.showAndWait();
    }
}
