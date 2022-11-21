import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PersistEmployeeArray {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/employee_db";
    static final String USER = "postgres";
    static final String PASSWORD = "postgres";

    public static void persist(Connection connection, Employee[] employees) throws SQLException {
        //Create an insert statement with placeholder ? for each column value
        String insertStatement = "INSERT INTO employee (id, email, office, salary) VALUES (?,?,?,?)";

        //Create a PreparedStatement object for sending parameterized SQL statements through the database connection.
        PreparedStatement preparedStmt = connection.prepareStatement(insertStatement);

        //Loop through employee array to insert one row for each object
        for (Employee e : employees) {
            // Assign prepared statement placeholders ? to employee object's fields
            preparedStmt.setInt(1, e.getId());   //first ?
            preparedStmt.setString(2, e.getEmail());  //second ?
            preparedStmt.setString(3, e.getOffice());  //third ?
            preparedStmt.setDouble(4, e.getSalary()); //fourth ?
            System.out.println(preparedStmt);

            //Execute the insert statement.
            preparedStmt.executeUpdate();
        }
    }

    public static void main(String[] args)  {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            //Create array of employees to save to the database
            Employee[] employees =  {
                new Employee(101, "emp101@company.com", "b301", 30000.0),
                new Employee(102, "emp102@company.com", "b350", 40000.0),
                new Employee(103, "emp103@company.com", "b400", 200000.0)
            };

            //Save each employee in the array to the database
            persist(connection, employees);

        connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}