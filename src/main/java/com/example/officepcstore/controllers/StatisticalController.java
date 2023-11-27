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
@RequestMapping("/api")
public class StatisticalController {
//    private final StatisticalService statisticalService;
//    @GetMapping(path = "/admin/manage/statistical/sale/total")
//    public ResponseEntity<?> getStats (@RequestParam(value = "from", defaultValue = "") String from,
//                                       @RequestParam(value = "to", defaultValue = "") String to,
//                                       @RequestParam("type") String type){
//        return statisticalService.getTotalSalesRevenue(from, to, type);
//    }

//    @GetMapping(path = "/admin/manage/statistical/sale/stat/total")
//    public ResponseEntity<?> getStatSales (@RequestParam(value = "year", defaultValue = "") int from,
//                                       @RequestParam(value = "month", defaultValue = "") int to)
//    {
//        return statisticalService.getOrderProductSales(from, to);
//    }
}
