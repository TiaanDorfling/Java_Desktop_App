package model;

import java.time.LocalDateTime;

/**
 * Represents an appointment between a student and a counselor
 */
public class Appointment {
    private int appointmentId;
    private int studentId;
    private int counselorId;
    private LocalDateTime appointmentDateTime;
    private String reason;
    private String status; // e.g., "Scheduled", "Completed", "Cancelled"
    private String notes;

    // Constructor
    public Appointment(int appointmentId, int studentId, int counselorId, 
                      LocalDateTime appointmentDateTime, String reason, 
                      String status, String notes) {
        this.appointmentId = appointmentId;
        this.studentId = studentId;
        this.counselorId = counselorId;
        this.appointmentDateTime = appointmentDateTime;
        this.reason = reason;
        this.status = status;
        this.notes = notes;
    }

    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(int counselorId) {
        this.counselorId = counselorId;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", studentId=" + studentId +
                ", counselorId=" + counselorId +
                ", appointmentDateTime=" + appointmentDateTime +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}



