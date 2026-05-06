package dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceDTO {

    private Integer empId;
    private LocalDate workDate;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private String empName;
    private String status;

    public AttendanceDTO() {
    }

    public AttendanceDTO(Integer empId, LocalDate workDate,
                         LocalTime checkIn, LocalTime checkOut) {
        this.empId = empId;
        this.workDate = workDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public Integer getEmpId() {
        return empId;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public LocalTime getCheckIn() {
        return checkIn;
    }

    public LocalTime getCheckOut() {
        return checkOut;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public void setCheckIn(LocalTime checkIn) {
        this.checkIn = checkIn;
    }

    public void setCheckOut(LocalTime checkOut) {
        this.checkOut = checkOut;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}