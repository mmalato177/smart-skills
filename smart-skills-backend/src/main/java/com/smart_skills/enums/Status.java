package com.smart_skills.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {

    ORDER("Order"),
    DESIGN("Design"),
    MANUFACTURING("Manufacturing"),
    QUALIFICATION_PROCESS("Qualification Process"),
    ENDURANCE_RUN("Endurance Run"),
    SAMPLING("Sampling"),
    WAITING_FOR_WAGO("Waiting For Wago"),
    CHANGES_BY_WAGO("Changes by Wago"),
    SPARE_PARTS_PRODUCTION("Spare Parts Production"),
    CORRECTION("Correction"),
    DONE("Done");


    private final String value;

    Status(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Status fromValue(String value) {
        for (Status status : Status.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}