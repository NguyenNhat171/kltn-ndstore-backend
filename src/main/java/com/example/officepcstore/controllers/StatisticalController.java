package com.example.officepcstore.controllers;

import com.example.officepcstore.service.StatisticalService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/manage/statistical")
public class StatisticalController {
    private final StatisticalService statisticalService;
    @GetMapping(path = "/sale/total")
    public ResponseEntity<?> getStats (@RequestParam(value = "from", defaultValue = "") String from,
                                       @RequestParam(value = "to", defaultValue = "") String to,
                                       @RequestParam("type") String type){
        return statisticalService.getTotalSalesRevenue(from, to, type);
    }
}