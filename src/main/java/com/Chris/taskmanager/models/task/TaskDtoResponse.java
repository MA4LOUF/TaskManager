package com.Chris.taskmanager.models.task;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Date;

public record TaskDtoResponse(String title, String description, Status status, Priority priority,
                              @JsonFormat(pattern = "MM/dd/yyyy")
                              LocalDate dueDate, String username, Date creation, Date lastUpdated) {}
