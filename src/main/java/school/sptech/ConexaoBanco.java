package school.sptech;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    public static Connection conectar() throws SQLException {

        String url = "jdbc:mysql://localhost:3306/aircode";
        String usuario = "root";
        String senha = "";

        return DriverManager.getConnection(
                url,
                usuario,
                senha
        );
    }
}