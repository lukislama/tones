package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class ConfirmationDialog
{

    ConfirmationDialog()
    {
    }

    public int show()
    {
        ButtonType first = new ButtonType("První");
        ButtonType second = new ButtonType("Druhý");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Chyba");
        alert.setHeaderText("Výběr tónu.");
        alert.setContentText("Který tón byl vyšší?");
        alert.getButtonTypes().setAll(first, second);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("confirm.png").toString()));

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == first)
            return 1;
        else if(result.get() == second)
            return 2;
        else
            return 0;
    }

}
