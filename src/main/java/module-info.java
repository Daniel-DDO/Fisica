module br.com.fisica.basicas {
    requires javafx.controls;
    requires javafx.fxml;


    opens br.com.fisica to javafx.fxml;
    exports br.com.fisica;
    exports br.com.fisica.basicas;
    opens br.com.fisica.basicas to javafx.fxml;
}