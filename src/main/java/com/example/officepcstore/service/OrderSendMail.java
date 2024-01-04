package com.example.officepcstore.service;

import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.models.enums.EnumMailType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
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
        String orderPay = "Chưa thanh toán hết";
        Map<String, Object> model = new HashMap<>();
        Locale locale = new Locale("vn", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        String totalPriceOrder = String.valueOf((orderSuccess.getTotalPrice().add(new BigDecimal(orderSuccess.getShipment().getServiceShipDetail().get("totalFeeShip").toString()))));

        model.put("orderId", orderSuccess.getId());
        model.put("total", currencyFormatter.format(orderSuccess.getTotalPrice()));
        model.put("paymentType", orderSuccess.getPaymentOrderMethod().getPaymentType());
        if ((boolean) orderSuccess.getPaymentOrderMethod().getTransactionInformation().get("fullPayment"))
            orderPay = "Đã thanh toán toàn bộ";
        model.put("payment", orderPay);
        model.put("name", orderSuccess.getShipment().getCustomerName());
        model.put("phone", orderSuccess.getShipment().getCustomerPhone());
        model.put("addressFull",orderSuccess.getShipment().getCustomerAddress());
        model.put("ward",orderSuccess.getShipment().getCustomerWard());
        model.put("district",orderSuccess.getShipment().getCustomerDistrict());
        model.put("province",orderSuccess.getShipment().getCustomerProvince());
        model.put("orderDate",orderSuccess.getInvoiceDate());
      model.put("feeShip",currencyFormatter.format(orderSuccess.getShipment().getServiceShipDetail().get("totalFeeShip")));
      model.put("totalFull",currencyFormatter.format(totalPriceOrder));
        Map<String, String> items = new HashMap<>();
        orderSuccess.getOrderDetails().forEach(item -> items.put(String.format("%s <br/> <b>[%s cái]</b>", item.getOrderProduct().getName(), item.getQuantity()), currencyFormatter.format(item.getProductOrderPrice())));
        model.put("items", items);
        model.put("subTotal", items);
        model.put("imgPro",items);
        sendMailService.sendEmail(orderSuccess.getUser().getEmail(), model, EnumMailType.ORDER_SUCCESS);
    }
}

