package com.example.officepcstore.config;

public class Constant {
    public static final String ENABLE = "enable";
    public static final String DISABLE = "disable";

    public static final String COMMENT_ENABLE = "Comment_Enable";
    public static final String COMMENT_BLOCK = "Comment_Block";
    //ROLE
    public static final String ROLE_ADMIN = "Role_Admin";
    public static final String ROLE_USER = "Role_User";

    //USER STATE
    public static final String USER_ACTIVE = "activated";
    public static final String USER_BLOCK = "blocked";
    public static final String USER_UNVERIFIED = "unconfimred";
    //ORDER STATE
    public static final String ORDER_CART = "cart";
    public static final String ORDER_CANCEL = "cancel";
    public static final String ORDER_PROCESS = "process";
    public static final String ORDER_SUCCESS = "success";
   public static final String ORDER_PROCESS_DELIVERY = "delivery";
   // public static final String ORDER_PREPARE = "prepare";
    public static final String ORDER_WAITING = "waiting"; // dang xu li

    //PAYMENT TYPE
    public static final String PAY_BY_PAYPAL = "paypal";
    public static final String PAY_BY_VNPAY = "vnpay";
    public static final String PAY_BY_COD = "cod";
    public static final int PAYMENT_TIMEOUT = 10 * 60 * 1500;

    //RECOMMEND TYPE
    public static final String REVIEW_GOOD_TYPE = "review";
    public static final String SET_SCORE_COMMENT ="comment";
    public static final String VIEW_TYPE = "view";
    public static final String CART_TYPE = "cart";
}
