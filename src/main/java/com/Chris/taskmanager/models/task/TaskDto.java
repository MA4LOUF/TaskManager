package com.Chris.taskmanager.models.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskDto(String title, String description, Status status, Priority priority,
                      @JsonFormat(pattern = "MM/dd/yyyy")
                      @Future
                      @NotNull
                      LocalDate dueDate) {}
