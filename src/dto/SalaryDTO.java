package dto;

/**
 * Data Transfer Object cho bảng Lương (Salaries).
 * Được thiết kế để lưu trữ lịch sử lương theo từng tháng/năm.
 */
public class SalaryDTO {
    private int empId;
    private int month;
    private int year;
    private double bonus;
    private double totalPenalty;
    private double finalAmount;

    // 1. Constructor không đối số (Mặc định)
    public SalaryDTO() {
    }

    // 2. Constructor đầy đủ đối số để khởi tạo nhanh khi tính toán xong
    public SalaryDTO(int empId, int month, int year, double baseSalary,
                     double allowance, double bonus, double totalPenalty, double finalAmount) {
        this.empId = empId;
        this.month = month;
        this.year = year;
        this.bonus = bonus;
        this.totalPenalty = totalPenalty;
        this.finalAmount = finalAmount;
    }

    // 3. Getters và Setters
    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public double getTotalPenalty() {
        return totalPenalty;
    }

    public void setTotalPenalty(double totalPenalty) {
        this.totalPenalty = totalPenalty;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    /**
     * Helper method để lấy số tiền định dạng VNĐ cho giao diện.
     * Giúp code ở View ngắn gọn và không bị tràn màn hình.
     */
    public String getFormattedFinalAmount() {
        return String.format("%,.0f VNĐ", finalAmount);
    }
}