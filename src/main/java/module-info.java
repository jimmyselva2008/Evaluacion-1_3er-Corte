module com.tuempresa.evaluacion1corte3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.tuempresa.evaluacion1corte3 to javafx.fxml;
    exports com.tuempresa.evaluacion1corte3;
}