package com.example.officepcstore.controllers;

import com.example.officepcstore.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ReportController {
    private final ReportService reportService;
    @GetMapping(path = "/admin/manage/report/sale/stat/total")
    public ResponseEntity<?> getReportProductSales (@RequestParam(value = "fromDay", defaultValue = "") String fromDay,
                                           @RequestParam(value = "toDay", defaultValue = "") String toDay)
    {
        return reportService.getOrderProductSalesReport(fromDay, toDay);
    }
}
