package com.smart_skills.controllers;

import com.smart_skills.enums.Company;
import com.smart_skills.enums.Status;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/status")
    public Status[] getStatus() {
        return Status.values();
    }

    @GetMapping("/company")
    public Company[] getCompany() {
        return Company.values();
    }
}

