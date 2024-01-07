package com.example.officepcstore.service;

import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.models.enums.EnumMailType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@Service
@Getter
@Setter
public class OrderSendMail implements  Runnable{
    private MailService sendMailService;
    private Order orderSuccess;
    @Override
    @SneakyThrows
    public void run() {

 LocalDateTime  inputDate = orderSuccess.getInvoiceDate();

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        ZoneId hoChiMinhZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime hoChiMinhDateTime = ZonedDateTime.of(inputDate, hoChiMinhZone);
        String formattedDate = hoChiMinhDateTime.format(outputFormatter);
        Map<String, Object> model = new HashMap<>();
        Locale locale = new Locale("vn", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        String totalPriceOrder = String.valueOf((orderSuccess.getTotalPrice().add(new BigDecimal(orderSuccess.getShipment().getServiceShipDetail().get("totalFeeShip").toString()))));
        model.put("orderId", orderSuccess.getId());
        model.put("total", currencyFormatter.format(orderSuccess.getTotalPrice()));
        model.put("paymentType", orderSuccess.getPaymentOrderMethod().getPaymentType());
        model.put("name", orderSuccess.getShipment().getCustomerName());
        model.put("phone", orderSuccess.getShipment().getCustomerPhone());
        model.put("addressFull",orderSuccess.getShipment().getCustomerAddress());
        model.put("ward",orderSuccess.getShipment().getCustomerWard());
        model.put("district",orderSuccess.getShipment().getCustomerDistrict());
        model.put("province",orderSuccess.getShipment().getCustomerProvince());
        model.put("orderDate",formattedDate);
      model.put("feeShip",currencyFormatter.format(orderSuccess.getShipment().getServiceShipDetail().get("totalFeeShip")));
      model.put("totalFull",currencyFormatter.format(new BigDecimal(totalPriceOrder)));
        Map<String, String> items = new HashMap<>();
        orderSuccess.getOrderDetails().forEach(item -> items.put(String.format("<img src = \"%s\" alt= \"\" style=\"width:40px;height:40px;\">  %s <br/> <b>Số lượng: %s cái</b>", item.getOrderProduct().getProductImageList().get(0).getUrl(),item.getOrderProduct().getName(), item.getQuantity()), currencyFormatter.format(item.getProductOrderPrice())));
        model.put("items", items);
        model.put("subTotal", items);
        model.put("imgPro",items);
        sendMailService.sendEmail(orderSuccess.getUser().getEmail(), model, EnumMailType.ORDER_SUCCESS);
    }
}

