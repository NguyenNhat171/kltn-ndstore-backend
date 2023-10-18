package com.example.officepcstore.service;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.models.enity.OrderedProduct;
import com.example.officepcstore.models.enity.product.Product;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.response.GoodsInventoryResponse;
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
import java.util.*;

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
        String typeDate = "dd-MM-yyyy";
        DateTimeFormatter df = DateTimeFormatter.ofPattern(typeDate);
        try {
            if (!from.isBlank()) startDate = LocalDate.parse(from, df).atStartOfDay();
            if (!to.isBlank()) endDate = LocalDate.parse(to, df).atStartOfDay();
        } catch (DateTimeParseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Incorrect date format");
        }
        Page<Order> orderList = orderRepository.findAllByInvoiceDateBetweenAndStatusOrder(startDate, endDate, Constant.ORDER_SUCCESS, Pageable.unpaged());
        switch (type) {
            case "all" -> {
                orderList = orderRepository.findAllByStatusOrder(Constant.ORDER_SUCCESS, PageRequest.of(0, Integer.MAX_VALUE, Sort.by("lastUpdateStateDate").ascending()));
                typeDate = "";
            }
            case "month" -> typeDate = "MM-yyyy";
            case "year" -> typeDate = "yyyy";
        }
        List<SaleResponse> totalSales = getTotalSales(orderList, typeDate);
        return totalSales.size() > 0 ? ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(true, "Statistical complete", totalSales)) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObjectData(false, "Cant not Statistical ", "")
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
                if (i == 0 || !ordersSaleRes.getDate().equals(dateFormat)) {
                    if (i > 0) ordersSaleResList.add(ordersSaleRes);
                    if (dateFormat.isBlank()) dateFormat = "all";
                    ordersSaleRes = new SaleResponse(dateFormat,
                            orderList.getContent().get(i).getTotalPriceOrder(), quantity);
                } else {
                    quantity++;
                    ordersSaleRes.setAmount(ordersSaleRes.getAmount().add(orderList.getContent().get(i).getTotalPriceOrder()));
                    ordersSaleRes.setQuantity(quantity);
                }
                if (i == orderList.getSize() - 1) ordersSaleResList.add(ordersSaleRes);
            }
        }
        return ordersSaleResList;
    }


    public  ResponseEntity<?> getOrderProductSales(int year, int month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1);
        Page<Order> orderList = orderRepository.findAllByInvoiceDateBetweenAndStatusOrder(startDate, endDate, Constant.ORDER_SUCCESS, Pageable.unpaged());
        Map<String, Long> SalesMap = new HashMap<>();
        for (Order order : orderList) {
            for (OrderedProduct orderedProduct : order.getOrderedProducts()) {
                String productId = orderedProduct.getOrderProduct().getId();
                long quantity = orderedProduct.getQuantity();
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
