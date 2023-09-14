package com.example.officepcstore.service;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.CartMap;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.models.enity.OrderedProduct;
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
            Optional<User> user = userRepository.findUserByIdAndState(userId, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            Optional<Order> order = orderRepository.findOrderByUser_IdAndState(new ObjectId(userId), Constant.ORDER_CART);
            if (order.isPresent()) {
                CartResponse res = cartMap.getProductCartRes(order.get());
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Get cart complete", res));
            } throw new NotFoundException("Not found cart  userid: "+userId);
        } throw new NotFoundException("Not found user with id: "+userId);
    }
//addAndUpdateProductToCart
    @Transactional
    public ResponseEntity<?> createAndPutProductToCart(String userId, CartReq req) {
        Optional<User> user = userRepository.findUserByIdAndState(userId, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            Optional<Order> order = orderRepository.findOrderByUser_IdAndState(new ObjectId(userId), Constant.ORDER_CART);
            if (order.isPresent()) {
                Optional<OrderedProduct> products = order.get().getOrderedProducts().stream().filter(
                        p -> p.getOrderProduct().getId().equals(req.getProductId())).findFirst();
                if (products.isPresent())
                    return countinueUpdateProductInCart(products.get(), req);
                else
                    return addProductToCartAvailable(order.get(), req);
            } else
                return createOrderByCart(user.get(), req);
        }
        throw new NotFoundException("Not found user with id: "+userId);
    }
//processAddProductToOrder
    @Transactional
    @Synchronized
    ResponseEntity<?> createOrderByCart(User user, CartReq req) {
        if (req.getQuantity() <= 0) throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid quantity");
        Optional<Product> product = productRepository.findById(req.getProductId());
        if (product.isPresent()) {
            checkProductQuantityAndStock(product.get(), req);
            Order order = new Order(user, Constant.ORDER_CART);
            orderRepository.insert(order);
            OrderedProduct orderedProduct = new OrderedProduct(product.get(), req.getQuantity(), order);
            orderProductRepository.insert(orderedProduct);
            CartProductResponse res = CartMap.toCartProductRes(orderedProduct);
//            addScoreToRecommendation(productOption.get().getProduct().getCategory().getId(),
//                    productOption.get().getProduct().getBrand().getId(), userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObjectData(true, "Product have add to cart first time complete", res));
        } else throw new NotFoundException("Not found product with id: "+req.getProductId());
    }
//processAddProductToExistOrder
    private ResponseEntity<?> addProductToCartAvailable(Order order, CartReq req) {
        Optional<Product> product = productRepository.findById(req.getProductId());
        if (product.isPresent()) {
            checkProductQuantityAndStock(product.get(), req);
            OrderedProduct orderedProduct = new OrderedProduct(product.get(), req.getQuantity(), order);
            orderProductRepository.insert(orderedProduct);
            CartProductResponse res = CartMap.toCartProductRes(orderedProduct);
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

    private ResponseEntity<?> countinueUpdateProductInCart(OrderedProduct orderedProduct, CartReq req) {
        if (orderedProduct.getQuantity() + req.getQuantity() == 0) {
            orderProductRepository.deleteById(orderedProduct.getId());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Remove item "+ orderedProduct.getId()+" in cart success", ""));
        }
                long quantity = orderedProduct.getQuantity() + req.getQuantity();
                if (orderedProduct.getOrderProduct().getStock() >= quantity && quantity > 0) {
                    orderedProduct.setQuantity(quantity);
                    orderProductRepository.save(orderedProduct);
                } else throw new AppException(HttpStatus.CONFLICT.value(), "Quantity exceeds stock this product: "+req.getProductId());

        CartProductResponse res = CartMap.toCartProductRes(orderedProduct);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(true, "Update product "+req.getProductId()+" complete", res));
    }


    public ResponseEntity<?> removeProductFromCart(String userId, String orderProductId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<OrderedProduct> orderProduct = orderProductRepository.findById(orderProductId);
            if (orderProduct.isPresent()){
                orderProductRepository.deleteById(orderProductId);
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Remove item "+orderProductId+" in cart complete", ""));
            }
            else throw new AppException(HttpStatus.NOT_FOUND.value(), "Not found product in cart");
        } throw new NotFoundException("Not found user with id: "+userId);
    }
}
