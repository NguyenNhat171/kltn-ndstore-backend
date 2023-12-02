package com.example.officepcstore.models.enity;


import com.example.officepcstore.models.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    @TextIndexed(weight = 8)
    private String name;
    @Indexed(unique = true)
    private String email;
    @JsonIgnore
    private String password;

    private int province;
    private int district;
    private int ward;
    private String address;
    @TextIndexed (weight = 4)
    private String phone;
    private String role;
    private String avatar;

    private AccountType accountType;

    private String statusUser;
    @ReadOnlyProperty
    @DocumentReference(lookup="{'user':?#{#self._id} }", lazy = true)
    @JsonIgnore
    @Indexed
    private List<Order> orders;
    @ReadOnlyProperty
    @DocumentReference(lookup="{'userReview':?#{#self._id} }", lazy = true)
    @JsonIgnore
    @Indexed
    private List<ReviewProduct> reviewProducts;
    @JsonIgnore
    private Token token;
    @JsonIgnore
    @Indexed
    private Map<Object, Integer> suggestedScore = new HashMap<>();
    @CreatedDate
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime createdDate;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @LastModifiedDate
    LocalDateTime lastUpdateStatus;

    public User(String name, String email, String password, String phone, Integer province, Integer district, Integer ward, String address, String role, String avatar, String statusUser, AccountType accountType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.address = address;
        this.role = role;
        this.avatar = avatar;
        this.statusUser = statusUser;
        this.accountType = accountType;
    }

    public User(String name, String email, String role, String avatar, AccountType accountType, String statusUser) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.avatar = avatar;
        this.accountType = accountType;
        this.statusUser = statusUser;
    }
}
