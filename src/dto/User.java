package dto;

public class User {
    private int id;
    private String username;
    private String password;
    private int role; // 0-admin,1-manager,2-employee
    private Integer empId;
    private String empName;
    public User(int id, String username, String password, int role, int empId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.empId = empId;

    }

    public int getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    public Integer getEmpId() {
        return empId;
    }

    public Object getId() {
        return id;
    }
    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }
}
