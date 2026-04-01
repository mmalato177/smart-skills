package com.smart_skills.models;

import com.smart_skills.enums.Company;
import com.smart_skills.enums.Status;
import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "moldes")
public class Molde {


    @Column(name = "WIS")
    private String wis;

    @Column(name = "Order")
    private String order;

    @Column(name = "PO date")
    private LocalDate poDate;

    @Column(name = "PO Delivery date")
    private LocalDate poDeliveryDate;

    @Column(name = "Anticipated delivery date")
    private LocalDate anticipatedDeliveryDate;

    @Column(name = "PO Qualification end date")
    private LocalDate poQualificationEndDate;

    @Column(name = "Anticipated qualification end date")
    private LocalDate anticipatedQualificationEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "Company")
    private Company company;

    @Column(name = "Date")
    private LocalDate date;

    @Column(name = "Anticipated end date")
    private LocalDate anticipatedEndDate;

    @Column(name = "Status Remarks")
    private String statusRemarks;

    @Column(name = "Responsible developer")
    private String responsibleDeveloper;

    @Id
    @Column(name = "WKZ-Bauer Nr.")
    private Long wkzBauerNr;


    @Column(name = "Image")
    private String image;

    public Molde() {
    }

    public String getWis() {
        return wis;
    }

    public void setWis(String wis) {
        this.wis = wis;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public LocalDate getPoDate() {
        return poDate;
    }

    public void setPoDate(LocalDate poDate) {
        this.poDate = poDate;
    }

    public LocalDate getPoDeliveryDate() {
        return poDeliveryDate;
    }

    public void setPoDeliveryDate(LocalDate poDeliveryDate) {
        this.poDeliveryDate = poDeliveryDate;
    }

    public LocalDate getAnticipatedDeliveryDate() {
        return anticipatedDeliveryDate;
    }

    public void setAnticipatedDeliveryDate(LocalDate anticipatedDeliveryDate) {
        this.anticipatedDeliveryDate = anticipatedDeliveryDate;
    }

    public LocalDate getPoQualificationEndDate() {
        return poQualificationEndDate;
    }

    public void setPoQualificationEndDate(LocalDate poQualificationEndDate) {
        this.poQualificationEndDate = poQualificationEndDate;
    }

    public LocalDate getAnticipatedQualificationEndDate() {
        return anticipatedQualificationEndDate;
    }

    public void setAnticipatedQualificationEndDate(LocalDate anticipatedQualificationEndDate) {
        this.anticipatedQualificationEndDate = anticipatedQualificationEndDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getAnticipatedEndDate() {
        return anticipatedEndDate;
    }

    public void setAnticipatedEndDate(LocalDate anticipatedEndDate) {
        this.anticipatedEndDate = anticipatedEndDate;
    }

    public String getStatusRemarks() {
        return statusRemarks;
    }

    public void setStatusRemarks(String statusRemarks) {
        this.statusRemarks = statusRemarks;
    }

    public String getResponsibleDeveloper() {
        return responsibleDeveloper;
    }

    public void setResponsibleDeveloper(String responsibleDeveloper) {
        this.responsibleDeveloper = responsibleDeveloper;
    }

    public Long getWkzBauerNr() {
        return wkzBauerNr;
    }

    public void setWkzBauerNr(Long wkzBauerNr) {
        this.wkzBauerNr = wkzBauerNr;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}