import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;

public class FindEmployee {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/employee_db";
    static final String USER = "postgres";
    static final String PASSWORD = "postgres";

    public static Employee find(Connection connection, int id)  {
        String selectStatement = "SELECT * FROM employee WHERE id = " + id;
        System.out.println(selectStatement);

        try (Statement statement = connection.createStatement()) {
            //Execute the select statement
            try (ResultSet rs = statement.executeQuery(selectStatement)) {

                //Proceed to the first row in the result set
                if (rs.next()) {
                    //Create an instance of class Employee using the row data
                    Employee employee = new Employee(rs.getInt("id"), rs.getString("email"), rs.getString("office"), rs.getDouble("salary"));

                    //Return the employee object
                    return employee;
                }
            } catch (SQLException e) { System.out.println(e.getMessage());}
        } catch (SQLException e) { System.out.println(e.getMessage());}

        //The employee was not in the table
        return null;
    }


    public static void main(String[] args)  {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            Employee employee;

            //Get employee with id=100
            employee = find(connection, 100);
            System.out.println(employee);  //Employee{id=100, email='emp100@company.com', office='a456', salary=99000.0}

            //Get non-existent employee id=99
            employee = find(connection, 99);
            System.out.println(employee); //null

        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }
}