package dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceDTO {

    private Integer empId;
    private LocalDate workDate;
    private LocalTime checkIn;
    private LocalTime checkOut;

    public AttendanceDTO(Integer empId, LocalDate workDate,
                         LocalTime checkIn, LocalTime checkOut) {
        this.empId = empId;
        this.workDate = workDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public Integer getEmpId() { return empId; }
    public LocalDate getWorkDate() { return workDate; }
    public LocalTime getCheckIn() { return checkIn; }
    public LocalTime getCheckOut() { return checkOut; }
}