import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTableStatement {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/employee_db";
    static final String USER = "postgres";
    static final String PASSWORD = "postgres";

    public static void main(String[] args) {
        try {
            //Establish a connection to the database
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            //Create a statement object to execute SQL statements on the database
            Statement statement = connection.createStatement();

            //Drop the employee table if it exists
            statement.execute("DROP TABLE IF EXISTS employee");

            //Create the employee table
            statement.execute("CREATE TABLE employee (id INTEGER PRIMARY KEY, email TEXT, office TEXT, salary DECIMAL)");
            System.out.println("Employee table created");

            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}



