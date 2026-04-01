package com.smart_skills.converters;

import com.smart_skills.enums.Company;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CompanyConverter implements AttributeConverter<Company, String> {

    @Override
    public String convertToDatabaseColumn(Company company) {
        return company != null ? company.getValue() : null;
    }

    @Override
    public Company convertToEntityAttribute(String dbData) {
        return dbData != null ? Company.fromValue(dbData) : null;
    }
}