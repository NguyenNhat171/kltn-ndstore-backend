package com.example.officepcstore.controllers;

import com.example.officepcstore.payload.request.ShipReq;
import com.example.officepcstore.service.LogisticService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class LogisticController {
    private final LogisticService logisticService;
    @PostMapping(path = "/logistic/fee", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFeeShip (@RequestBody ShipReq req) {

        return logisticService.calculateFee(req);
    }
    @PostMapping(path = "/logistic/service", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getService (@RequestBody ShipReq req) {

        return logisticService.getService(req);
    }
    @PostMapping(path = "/logistic/detail/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetail (@PathVariable String orderId) {

        return logisticService.getDetail(orderId);
    }

    @PostMapping(path = "/logistic/province", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProvince () {

        return logisticService.getProvince();
    }

    @PostMapping(path = "/logistic/district", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDistrict (@RequestBody ShipReq req) {
        return logisticService.getDistrict(req.getProvince_id());
    }

    @PostMapping(path = "/logistic/ward", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWard (@RequestBody ShipReq req) {
        return logisticService.getWard(req.getDistrict_id());
    }

    @PostMapping(path = "/logistic/expectedTime", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> calculateExpectedDeliveryTime (@RequestBody ShipReq req) {
        return logisticService.calculateExpectedDeliveryTime(req);
    }

}
