package com.example.officepcstore.repository;



import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.models.enity.product.Product;
import com.example.officepcstore.models.enums.AccountType;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findUserByEmailAndStatusUser(String email, String state);
    Optional<User> findUserByEmail(String email);
   // Optional<User> findUserByNameIgnoreCase(String name);
    Page<User> findUserByRole(String userRole,Pageable pageable);
    Page<User> findUsersByEmail(String email,Pageable pageable);
    Optional<User> findUserByIdAndStatusUser(String id, String state);
   Page<User> findAllByStatusUser(String state, Pageable pageable);
   Page<User> findAllByStatusUserAndRole(String status,String role,Pageable pageable);
   boolean existsByEmail(String email);
   Optional<User> findUserByEmailAndAccountType(String email, AccountType type);


    Page<User> findAllBy(TextCriteria textCriteria, Pageable pageable);

Page<User>findAllByEmailLikeIgnoreCase(String email,Pageable pageable);


}
