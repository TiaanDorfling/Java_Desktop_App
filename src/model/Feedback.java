package model;

import java.time.LocalDateTime;

/**
 * Represents feedback submitted by a student about a counseling appointment
 */
public class Feedback {
    private int feedbackId;
    private int appointmentId;
    private int studentId;
    private int counselorId;
    private int rating; // Typically 1-5 scale
    private String comments;
    private LocalDateTime submissionDate;
    private boolean isAnonymous;

    // Constructors
    public Feedback() {
        // Default constructor
    }

    public Feedback(int feedbackId, int appointmentId, int studentId, int counselorId, 
                   int rating, String comments, LocalDateTime submissionDate, 
                   boolean isAnonymous) {
        this.feedbackId = feedbackId;
        this.appointmentId = appointmentId;
        this.studentId = studentId;
        this.counselorId = counselorId;
        this.rating = rating;
        this.comments = comments;
        this.submissionDate = submissionDate;
        this.isAnonymous = isAnonymous;
    }

    // Getters and Setters
    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        if (comments != null && comments.length() > 500) {
            throw new IllegalArgumentException("Comments cannot exceed 500 characters");
        }
        this.comments = comments;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    // Utility methods
    public boolean isValid() {
        return rating >= 1 && rating <= 5 && 
               comments != null && !comments.trim().isEmpty() &&
               submissionDate != null &&
               appointmentId > 0;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackId=" + feedbackId +
                ", appointmentId=" + appointmentId +
                ", studentId=" + (isAnonymous ? "ANONYMOUS" : studentId) +
                ", counselorId=" + counselorId +
                ", rating=" + rating +
                ", comments='" + (comments.length() > 20 ? comments.substring(0, 20) + "..." : comments) + '\'' +
                ", submissionDate=" + submissionDate +
                ", isAnonymous=" + isAnonymous +
                '}';
    }
}
