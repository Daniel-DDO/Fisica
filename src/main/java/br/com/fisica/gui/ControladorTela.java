package br.com.fisica.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class ControladorTela implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inicializarCbox();
    }

    private static ControladorTela instancia;

    public static ControladorTela getInstancia() {
        if (instancia == null) {
            instancia = new ControladorTela();
        }
        return instancia;
    }

    Image circuitoCAlternada = new Image(getClass().getResourceAsStream("/br/com/fisica/images/C-Alternada.png"));

    Image circuitoCContinua = new Image(getClass().getResourceAsStream("/br/com/fisica/images/C-Continua.png"));

    @FXML
    private ImageView imageViewCircuito;

    @FXML
    private ComboBox<String> cboxSelecionarFonte;

    @FXML
    private Button confirmarFonteButton;

    public void inicializarCbox() {
        cboxSelecionarFonte.getItems().add("Corrente Contínua (CC)");
        cboxSelecionarFonte.getItems().add("Corrente Alternada (CA)");
    }

    public void selecionarCircuito() {
        if (cboxSelecionarFonte.getValue() != null) {
            if (cboxSelecionarFonte.getValue().equals("Corrente Contínua (CC)")) {
                imageViewCircuito.setImage(circuitoCContinua);
                System.out.println("continua");
            } else if (cboxSelecionarFonte.getValue().equals("Corrente Alternada (CA)")) {
                imageViewCircuito.setImage(circuitoCAlternada);
                System.out.println("alternada");
            }
        }
    }

    @FXML
    void buttonConfirmarFonte(ActionEvent event) {
        selecionarCircuito();
    }

}
