import java.util.List;

public class SalaryInfo {
    private String info;
    private List<Employee> employees;

    public SalaryInfo(String info, List<Employee> employees) {
        this.info = info;
        this.employees = employees;
    }

    public SalaryInfo() {
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}

