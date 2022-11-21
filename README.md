# JDBC Basic Object Mapping

## Learning Goals

- Define Object-Relational Mapping
- Save an `Employee` class instance to a database table.
- Create an `Employee` class instance from the data in a table row.
- Use the `PreparedStatement` interface to efficiently insert multiple `Employee` objects into a table.

## Introduction

**Object Relational Mapping (ORM)** is a programming technique for converting
between a relational data model and an object-oriented data model.

In a subsequent lesson we will work with the **Jakarta Persistence API (JPA)**,
which is a standard specification that defines how an ORM should interact with a relational database.
In this lesson we will cover a very simple implementation of persistence that will
let us save an `Employee` class instance to a table, and subsequently create an `Employee`
class instance by querying and retrieving a row of data from the table.

## Code along

[Fork and clone](https://github.com/learn-co-curriculum/java-mod-5-jdbc-object-mapping) this lesson to run the sample Java programs.

NOTE: This lesson assumes you created a new PostgreSQL database named `employee_db` as instructed
in the first lesson.

The lesson files include the `CreateTableStatement`  class from the previous lesson.  

1. Run `CreateTableStatement.main` to create an empty `employee` table.
2. Query the `employee` table in the **pgAdmin** query tool to confirm an empty table:

![new employee table](https://curriculum-content.s3.amazonaws.com/6036/jdbc-statement/empty_table.png)

## Saving an `Employee` object to a table 

We will store an `Employee` object into the `employee` table.   Notice the instance variable
data types correspond to the column data types in the table.

```java
public class Employee {
    private int id;
    private String email;
    private String office;
    private double salary;

    public Employee(int id, String email, String office, double salary) {
        this.id = id;
        this.email = email;
        this.office = office;
        this.salary = salary;
    }

    public int getId()        {return id;}
    public void setId(int id) { this.id = id; }
    public String getEmail()  {return email;}
    public void setEmail(String email) { this.email = email; }
    public String getOffice() {return office;}
    public void setOffice(String office) { this.office = office; }
    public double getSalary() {return salary;}
    public void setSalary(double salary) { this.salary = salary; }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", office='" + office + '\'' +
                ", salary=" + salary +
                '}';
    }
}
```

The `PersistEmployee` class  shown below has a method named `persist` that stores
an `Employee` class instance in the `employee` table.  

The method executes an SQL `INSERT`
statement using the instance variable values of the `Employee` object passed as a parameter: 

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
```

Run `PersistEmployee.main` to confirm the output, which shows the INSERT statement that is executed:

```text
INSERT INTO employee (id, email, office, salary) VALUES (100, 'emp100@company.com', 'a456', 99000.00)
```

Query the table in the query tool to confirm the new row:

![1 row persisted](https://curriculum-content.s3.amazonaws.com/6036/jdbc-object-mapping/1employee.png)


## Creating an `Employee` class instance from a table row 

Now we will see how to create an `Employee` class instance from a row returned by a query.

The `FindEmployee` class  shown below has a method named `find` that retrieves 
an `Employee` class instance by querying the `employee` table with a specific value for
the primary key `id`.  The column data in the resulting row is used to instantiate
an `Employee` class instance:

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindEmployee {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/employee_db";
    static final String USER = "postgres";
    static final String PASSWORD = "postgres";

    public static Employee find(Connection connection, int id)  {
        try {
            String selectStatement = "SELECT * FROM employee WHERE id = " + id;
            System.out.println(selectStatement);
            
            //Execute the select statement
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectStatement);
            
            //Proceed to the first row in the result set
            if (rs.next()) {
                //Create an instance of class Employee using the row data
                Employee employee = new Employee(rs.getInt("id"), rs.getString("email"), rs.getString("office"), rs.getDouble("salary"));;

                //Close the result set
                rs.close();

                //Return the employee object
                return employee;
            }

        }
        catch (SQLException e) { System.out.println(e.getMessage());}

        //The employee was not in the table
        return null;
    }


    public static void main(String[] args)  {
        try {
            Connection connection  = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            Employee employee;

            //Get employee with id=100
            employee = find(connection, 100);
            System.out.println(employee);  //Employee{id=100, email='emp100@company.com', office='a456', salary=99000.0}

            //Get non-existent employee id=99
            employee = find(connection, 99);
            System.out.println(employee); //null

            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
```

Run `FindEmployee.main` to confirm the output, which shows the SELECT statement that is executed
for each call to the `find` method:

```text
SELECT * FROM employee WHERE id = 100
Employee{id=100, email='emp100@company.com', office='a456', salary=99000.0}
SELECT * FROM employee WHERE id = 99
null
```


## Saving an array of `Employee` objects



The JDBC `PreparedStatement` interface is a special kind of  `Statement` that improves performance
when an SQL statement needs to be executed multiple times:

- The SQL statement may include value placeholders or parameters using the `?` character.
- The SQL statement may be reused with new parameter values specified for each `?`.

The `PersistEmployeeArray` class below contains a `persist` method that takes an `Employee` array
as a parameter, and uses the `PreparedStatement` interface to add each employee to the table
using the same statement object.

- The  method creates an instance of `PreparedStatement` that contains
  placeholders `?` for each column in the `employee` table.
- The loop assigns the placeholders to the instance variable values of the next `Employee` object in the array.
- The prepared statement object executes the insert statement to insert the employee row. 


```java
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
```

Run `PersistEmployeeArray.main` to confirm the output, which shows the INSERT statement that is executed
for each array item:

```text
INSERT INTO employee (id, email, office, salary) VALUES (101,'emp101@company.com','b301',30000.0)
INSERT INTO employee (id, email, office, salary) VALUES (102,'emp102@company.com','b350',40000.0)
INSERT INTO employee (id, email, office, salary) VALUES (103,'emp103@company.com','b400',200000.0)
```

Query the table in the query tool to confirm the 3 new rows (in addition to the row previously inserted):

![prepare statement](https://curriculum-content.s3.amazonaws.com/6036/jdbc-object-mapping/preparestatement_result.png)



## Conclusion

**Object Relational Mapping (ORM)** is a technique for converting
between a relational data model and an object-oriented data model.
This lesson stepped through a very basic example of ORM, demonstrating
how to save an object's state into a table, and how to extract data from a table
and store it as object state.  We also looked at the `PreparedStatement` interface, which
can be used to efficiently execute an SQL statement multiple times with different values.

## Resources

- [Statement](https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html)   
- [PreparedStatement](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html)   
- [JDBC API](https://docs.oracle.com/javase/8/docs/api/java/sql/package-summary.html)  
- [Hibernate ORM](https://hibernate.org/)    
- [Java ORMs](https://en.wikipedia.org/wiki/List_of_object%E2%80%93relational_mapping_software#Java)    

