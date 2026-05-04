package dto;

import java.util.Date;

public class EmployeeDetailDTO {
    private int empId;
    private java.util.Date birthday;
    private Integer gender; // 1: Nam, 0: Nữ, 2: Khác
    private String idCard;
    private String address;
    private String avatar;
    private String education;
    private String experience;

    // Constructors, Getters và Setters

    public EmployeeDetailDTO(int empId, Date birthday, Integer gender, String idCard, String address, String avatar, String education, String experience) {
        this.empId = empId;
        this.birthday = birthday;
        this.gender = gender;
        this.idCard = idCard;
        this.address = address;
        this.avatar = avatar;
        this.education = education;
        this.experience = experience;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }
}