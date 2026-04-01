package com.smart_skills.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Company {
    TECNIFREZA("Tecnifreza"),
    DRT("DRT"),
    FAMOLDE("Famolde"),
    MOLIPOREX("Moliporex");

    private final String value;

    Company(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Company fromValue(String value) {
        for (Company company : Company.values()) {
            if (company.value.equalsIgnoreCase(value)) {
                return company;
            }
        }
        throw new IllegalArgumentException("Unknown company: " + value);
    }
}
