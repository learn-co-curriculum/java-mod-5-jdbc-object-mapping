import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PersistEmployee {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/employee_db";
    static final String USER = "postgres";
    static final String PASSWORD = "postgres";

    public static void persist(Connection connection, Employee employee) throws SQLException {
        //Prepare the insert statement string using the Employee instance variable values
        String insertStatement = String.format("INSERT INTO employee (id, email, office, salary) VALUES (%d, \'%s\', \'%s\', %.2f)",
                                 employee.getId(), employee.getEmail(), employee.getOffice(), employee.getSalary());
        System.out.println(insertStatement);

        //Execute the insert statement
        Statement statement = connection.createStatement();
        statement.executeUpdate(insertStatement);
    }

    public static void main(String[] args)  {
        try {
            Connection connection =  DriverManager.getConnection(DB_URL, USER, PASSWORD);

            //Create a new Employee object
            Employee employee = new Employee(100, "emp100@company.com", "a456", 99000.0);

            //Save the object to the database
            persist(connection, employee);

            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}