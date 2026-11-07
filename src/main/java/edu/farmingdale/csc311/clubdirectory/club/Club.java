package edu.farmingdale.csc311.clubdirectory.club;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "name is required")
    @Size(max = 60, message = "name must be at most 60 characters")
    private String name;

    @NotBlank(message = "category is required")
    @Size(max = 30, message = "category must be at most 30 characters")
    private String category;

    @NotBlank(message = "meetingDay is required")
    @Size(max = 20, message = "meetingDay must be at most 20 characters")
    private String meetingDay;

    @Size(max = 40, message = "room must be at most 40 characters")
    private String room;

    protected Club() {
    }

    public Club(String name, String category, String meetingDay, String room) {
        this.name = strip(name);
        this.category = strip(category);
        this.meetingDay = strip(meetingDay);
        this.room = stripToNull(room);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = strip(name);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = strip(category);
    }

    public String getMeetingDay() {
        return meetingDay;
    }

    public void setMeetingDay(String meetingDay) {
        this.meetingDay = strip(meetingDay);
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = stripToNull(room);
    }

    private static String strip(String value) {
        return value == null ? null : value.strip();
    }

    // Room is optional, so a blank room means "no room" rather than an empty string.
    private static String stripToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
