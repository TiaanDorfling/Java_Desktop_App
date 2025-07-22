package model;

import java.util.List;

/**
 * Represents a counselor in the counseling appointment system
 */
public class Counselor {
    private int counselorId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String specialization;
    private List<String> availableDays;
    private String workingHours;
    private boolean isActive;
    private String bio;

    // Constructors
    public Counselor() {
        // Default constructor
    }

    public Counselor(int counselorId, String firstName, String lastName, 
                   String email, String phoneNumber, String specialization,
                   List<String> availableDays, String workingHours, 
                   boolean isActive, String bio) {
        this.counselorId = counselorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.specialization = specialization;
        this.availableDays = availableDays;
        this.workingHours = workingHours;
        this.isActive = isActive;
        this.bio = bio;
    }

    // Getters and Setters
    public int getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(int counselorId) {
        this.counselorId = counselorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        this.phoneNumber = phoneNumber;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public List<String> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(List<String> availableDays) {
        this.availableDays = availableDays;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAvailableOn(String day) {
        return availableDays != null && availableDays.contains(day);
    }

    @Override
    public String toString() {
        return "Counselor{" +
                "counselorId=" + counselorId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", specialization='" + specialization + '\'' +
                ", availableDays=" + availableDays +
                ", workingHours='" + workingHours + '\'' +
                ", isActive=" + isActive +
                ", bio='" + (bio != null && bio.length() > 20 ? bio.substring(0, 20) + "..." : bio) + '\'' +
                '}';
    }
}
