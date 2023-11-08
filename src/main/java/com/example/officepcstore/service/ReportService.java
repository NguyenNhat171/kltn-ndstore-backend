package com.example.officepcstore.service;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.models.enity.OrderDetail;
import com.example.officepcstore.models.enity.product.Product;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.response.GoodsInventoryResponse;
import com.example.officepcstore.repository.OrderRepository;
import com.example.officepcstore.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ReportService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    public ResponseEntity<?> getOrderProductSalesReport(String beginDay, String endDay) {

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        String typeDate = "dd-MM-yyyy";
        DateTimeFormatter df = DateTimeFormatter.ofPattern(typeDate);
        try {
            if (!beginDay.isBlank()) startDate = LocalDate.parse(beginDay, df).atStartOfDay();
            if (!endDay.isBlank()) endDate = LocalDate.parse(endDay, df).atStartOfDay();
        } catch (DateTimeParseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Incorrect date format");
        }
        Page<Order> orderList = orderRepository.findAllByInvoiceDateBetweenAndStatusOrder(startDate, endDate, Constant.ORDER_SUCCESS, Pageable.unpaged());
        Map<String, Long> SalesMap = new HashMap<>();
        for (Order order : orderList) {
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                String productId = orderDetail.getOrderProduct().getId();
                long quantity = orderDetail.getQuantity();
                SalesMap.put(productId, SalesMap.getOrDefault(productId, 0L) + quantity);
            }
        }
        List<GoodsInventoryResponse> saleList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : SalesMap.entrySet()) {
            String productId = entry.getKey();
            long count= entry.getValue();
            Product product = productRepository.findById(productId).orElse(null);

            if (product != null) {
                GoodsInventoryResponse goodsInventoryResponse= new GoodsInventoryResponse();
                goodsInventoryResponse.setId(productId);
                goodsInventoryResponse.setName(product.getName());
                goodsInventoryResponse.setSalable(count);
                saleList.add(goodsInventoryResponse);
            }
        }
        saleList.sort(Comparator.comparing(GoodsInventoryResponse::getSalable).reversed());
        return saleList.size()>0 ? ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(true, "Statistical complete", saleList)
        ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObjectData(false, "Cant not Statistical ", "")
                );
    }


    public ResponseEntity<?> getOrderSale(String beginDay, String endDay) {

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        String typeDate = "dd-MM-yyyy";
        DateTimeFormatter df = DateTimeFormatter.ofPattern(typeDate);
        try {
            if (!beginDay.isBlank()) startDate = LocalDate.parse(beginDay, df).atStartOfDay();
            if (!endDay.isBlank()) endDate = LocalDate.parse(endDay, df).atStartOfDay();
        } catch (DateTimeParseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Incorrect date format");
        }
        Page<Order> orderList = orderRepository.findAllByInvoiceDateBetweenAndStatusOrder(startDate, endDate, Constant.ORDER_SUCCESS, Pageable.unpaged());
        Map<String, Long> SalesMap = new HashMap<>();
        for (Order order : orderList) {
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                String productId = orderDetail.getOrderProduct().getId();
                long quantity = orderDetail.getQuantity();
                SalesMap.put(productId, SalesMap.getOrDefault(productId, 0L) + quantity);
            }
        }
        List<GoodsInventoryResponse> saleList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : SalesMap.entrySet()) {
            String productId = entry.getKey();
            long count= entry.getValue();
            Product product = productRepository.findById(productId).orElse(null);

            if (product != null) {
                GoodsInventoryResponse goodsInventoryResponse= new GoodsInventoryResponse();
                goodsInventoryResponse.setId(productId);
                goodsInventoryResponse.setName(product.getName());
                goodsInventoryResponse.setSalable(count);
                saleList.add(goodsInventoryResponse);
            }
        }
        saleList.sort(Comparator.comparing(GoodsInventoryResponse::getSalable).reversed());
        return saleList.size()>0 ? ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(true, "Statistical complete", saleList)
        ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObjectData(false, "Cant not Statistical ", "")
                );
    }
}
