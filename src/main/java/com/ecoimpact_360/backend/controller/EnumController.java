package com.ecoimpact_360.backend.controller;

import com.ecoimpact_360.backend.model.enums.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/all")
    public Map<String, Object> getAllEnums() {
        return Map.of(
            "categories", WasteCategory.values(),
            "status", WasteStatus.values(),
            "alertTypes", AlertType.values()
        );
    }
}