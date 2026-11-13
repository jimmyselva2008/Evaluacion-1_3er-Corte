
module com.tuempresa.evaluacion1corte3 {

    requires javafx.controls;
    requires javafx.fxml;

    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    requires java.sql;

    opens com.tuempresa.evaluacion1corte3
            to javafx.fxml;

    opens com.tuempresa.evaluacion1corte3.controller
            to javafx.fxml;

    opens com.tuempresa.evaluacion1corte3.model
            to org.hibernate.orm.core, javafx.base;

    exports com.tuempresa.evaluacion1corte3;

    exports com.tuempresa.evaluacion1corte3.controller;

    exports com.tuempresa.evaluacion1corte3.model;
}
