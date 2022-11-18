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

    public int getId() {
        return id;
    }
    public void setId(int id) { this.id = id; }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) { this.email = email; }
    public String getOffice() {
        return office;
    }
    public void setOffice(String office) { this.office = office; }
    public double getSalary() {
        return salary;
    }
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
