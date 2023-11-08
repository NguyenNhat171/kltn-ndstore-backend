package com.example.officepcstore.service;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.CartMap;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.models.enity.OrderDetail;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.models.enity.product.Product;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.CartReq;
import com.example.officepcstore.payload.response.CartProductResponse;
import com.example.officepcstore.payload.response.CartResponse;
import com.example.officepcstore.repository.OrderProductRepository;
import com.example.officepcstore.repository.OrderRepository;
import com.example.officepcstore.repository.ProductRepository;
import com.example.officepcstore.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CartService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final CartMap cartMap;
  //  private final RecommendCheckUtils recommendCheckUtils;
    private final TaskScheduler taskScheduler;

    public ResponseEntity<?> getProductFromCart(String userId) {
            Optional<User> user = userRepository.findUserByIdAndStatusUser(userId, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            Optional<Order> order = orderRepository.findOrderByUser_IdAndStatusOrder(new ObjectId(userId), Constant.ORDER_CART);
            if (order.isPresent()) {
                CartResponse res = cartMap.getProductCartRes(order.get());
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Get cart complete", res));
            }
            else
                return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Get cart complete", " "));
        } return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found user"+userId, " "));
    }

    @Transactional
    public ResponseEntity<?> createAndCheckProductInCart(String userId, CartReq req) {
        Optional<User> user = userRepository.findUserByIdAndStatusUser(userId, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            Optional<Order> order = orderRepository.findOrderByUser_IdAndStatusOrder(new ObjectId(userId), Constant.ORDER_CART);
            if (order.isPresent()) {
                Optional<OrderDetail> products = order.get().getOrderDetails().stream().filter(
                        p -> p.getOrderProduct().getId().equals(req.getProductId())).findFirst();
                if (products.isPresent())
                    return continueUpdateQuantityProductCart(products.get(), req);
                else
                    return putProductToCartAvailable(order.get(), req);
            } else
                return createCart(user.get(), req);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found user"+userId, " "));
    }

    @Transactional
    @Synchronized
    ResponseEntity<?> createCart(User user, CartReq req) {
        if (req.getQuantity() <= 0) throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid quantity");
        Optional<Product> product = productRepository.findById(req.getProductId());
        if (product.isPresent()) {
            checkProductQuantityAndStock(product.get(), req);
            Order order = new Order(user, Constant.ORDER_CART);
            orderRepository.insert(order);
            OrderDetail orderDetail = new OrderDetail(product.get(), req.getQuantity(), order);
            orderProductRepository.insert(orderDetail);
            CartProductResponse res = CartMap.toCartProductRes(orderDetail);
//            addScoreToRecommendation(productOption.get().getProduct().getCategory().getId(),
//                    productOption.get().getProduct().getBrand().getId(), userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObjectData(true, "Product have add to cart first time complete", res));
        } else throw new NotFoundException("Not found product with id: "+req.getProductId());
    }

    private ResponseEntity<?> putProductToCartAvailable(Order order, CartReq req) {
        Optional<Product> product = productRepository.findById(req.getProductId());
        if (product.isPresent()) {
            checkProductQuantityAndStock(product.get(), req);
            OrderDetail orderDetail = new OrderDetail(product.get(), req.getQuantity(), order);
            orderProductRepository.insert(orderDetail);
            CartProductResponse res = CartMap.toCartProductRes(orderDetail);
//            addScoreToRecommendation(productOption.get().getProduct().getCategory().getId(),
//                    productOption.get().getProduct().getBrand().getId(), userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObjectData(true, "Add product to cart complete", res));
        } else throw new NotFoundException("Not found product  id: "+req.getProductId());
    }


    private void checkProductQuantityAndStock(Product product, CartReq req) {

            if (product.getStock() < req.getQuantity()) {
                throw new AppException(HttpStatus.CONFLICT.value(), "Quantity exceeds stock: "+req.getProductId()
                        + " with Product name:"+ product.getName() + " this Product stock:" + product.getStock());
            }
    }

    private ResponseEntity<?> continueUpdateQuantityProductCart(OrderDetail orderDetail, CartReq req) {
        if (orderDetail.getQuantity() + req.getQuantity() == 0) {
            orderProductRepository.deleteById(orderDetail.getId());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Remove item "+ orderDetail.getId()+" in cart success", ""));
        }
                long quantity = orderDetail.getQuantity() + req.getQuantity();
                if (orderDetail.getOrderProduct().getStock() >= quantity && quantity > 0) {
                    orderDetail.setQuantity(quantity);
                    orderProductRepository.save(orderDetail);
                } else throw new AppException(HttpStatus.CONFLICT.value(), "Quantity exceeds stock this product: "+req.getProductId());

        CartProductResponse res = CartMap.toCartProductRes(orderDetail);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(true, "Update product "+req.getProductId()+" complete", res));
    }


    public ResponseEntity<?> removeProductFromCart(String userId, String orderProductId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<OrderDetail> orderProduct = orderProductRepository.findById(orderProductId);
            if (orderProduct.isPresent()){
                orderProductRepository.deleteById(orderProductId);
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Remove item "+orderProductId+" in cart complete", ""));
            }
            else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Remove item failed because "+orderProductId+"not in cart", ""));
        } return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found user"+userId, " "));
    }
}
