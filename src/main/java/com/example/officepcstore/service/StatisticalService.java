package com.example.officepcstore.service;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.response.SaleResponse;
import com.example.officepcstore.repository.CategoryRepository;
import com.example.officepcstore.repository.OrderRepository;
import com.example.officepcstore.repository.ProductRepository;
import com.example.officepcstore.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class StatisticalService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<?> getTotalSalesRevenue(String from, String to, String type) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        String typeDate= "dd-MM-yyyy";
        DateTimeFormatter df = DateTimeFormatter.ofPattern(typeDate);
        try {
            if (!from.isBlank()) startDate = LocalDate.parse(from, df).atStartOfDay();
            if (!to.isBlank()) endDate = LocalDate.parse(to, df).atStartOfDay();
        } catch (DateTimeParseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Incorrect date format");
        }
        Page<Order> orderList = orderRepository.findAllByCreatedDateBetweenAndState(startDate, endDate, Constant.ORDER_COMPLETE, Pageable.unpaged());
        switch (type) {
            case "all" -> {
                orderList = orderRepository.findAllByState(Constant.ORDER_COMPLETE, PageRequest.of(0, Integer.MAX_VALUE, Sort.by("lastModifiedDate").ascending()));
                typeDate = "";
            }
            case "month" ->typeDate = "MM-yyyy";
            case "year" -> typeDate = "yyyy";
        }
        List<SaleResponse> totalSales = getTotalSales(orderList, typeDate);
        return totalSales.size() > 0 ? ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(true, "Statistical successful", totalSales )) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObjectData(false, "Statistical Fail", "")
                );
    }

    public List<SaleResponse> getTotalSales(Page<Order> orderList, String pattern) {
        List<SaleResponse> ordersSaleResList = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        if (orderList.getSize() > 0) {
            SaleResponse ordersSaleRes = new SaleResponse();
            int quantity = 1;
            for (int i = 0; i <= orderList.getSize() - 1; i++) {
                String dateFormat = df.format(orderList.getContent().get(i).getLastUpdateStateDate());
                if (i == 0 || !ordersSaleRes.getDate().equals(dateFormat))
                {
                    if (i > 0) ordersSaleResList.add(ordersSaleRes);
                    if (dateFormat.isBlank()) dateFormat = "all";
                    ordersSaleRes = new SaleResponse(dateFormat,
                            orderList.getContent().get(i).getTotalPrice(), quantity);
                } else {
                    quantity++;
                    ordersSaleRes.setAmount(ordersSaleRes.getAmount().add(orderList.getContent().get(i).getTotalPrice()));
                    ordersSaleRes.setQuantity(quantity);
                }
                if (i == orderList.getSize() - 1) ordersSaleResList.add(ordersSaleRes);
            }
        }
        return ordersSaleResList;
    }
}
