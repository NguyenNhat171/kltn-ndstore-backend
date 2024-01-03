package com.example.officepcstore.service;

import com.example.officepcstore.models.enity.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@Getter
@Setter
public class OrderSendMail implements  Runnable{
    private MailService sendMail;
    private Order orderSuccess;
    @Override
    @SneakyThrows
    public void run() {
//        String paid = "Chưa thanh toán";
//        Map<String, Object> model = new HashMap<>();
//        Locale locale = new Locale("vn", "VN");
//        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
//
//
//        model.put("orderId", orderSuccess.getId());
//        model.put("total", currencyFormatter.format(orderSuccess.getTotalPrice()));
//        model.put("paymentType", orderSuccess.getPaymentDetail().getPaymentType());
//        if ((boolean) orderSuccess.getPaymentDetail().getPaymentInfo().get("isPaid"))
//            paid = "Đã thanh toán";
//        model.put("isPaid", paid);
//        model.put("name", orderSuccess.getDeliveryDetail().getReceiveName());
//        model.put("phone", orderSuccess.getDeliveryDetail().getReceivePhone());
//        model.put("address",orderSuccess.getDeliveryDetail().getDeliveryInfo().getOrDefault("fullAddress", order.getDeliveryDetail().getReceiveAddress()));
//        try {
//            model.put("expectedTime", LocalDate.ofInstant
//                    (Instant.ofEpochMilli((Long) orderSuccess.getDeliveryDetail().getDeliveryInfo().get("expectedDeliveryTime")*1000),
//                            TimeZone.getDefault().toZoneId()));
//        } catch (Exception e) {
//            model.put("expectedTime", LocalDate.now().plusDays(3));
//        }
//
//        Map<String, String> items = new HashMap<>();
//        orderSuccess.getOrderDetails().forEach(item -> items.put(String.format("%s <br/> <b>[%s cái]</b>", item.getItem().getProduct().getName(), item.getQuantity()), currencyFormatter.format(item.getSubPrice())));
//        model.put("items", items);
//        model.put("feeShip",orderSuccess.getPaymentOrderMethod().)
//        model.put("subTotal", items);
//        mailService.sendEmail(order.getUser().getEmail(), model, EMailType.ORDER);
    }
}

