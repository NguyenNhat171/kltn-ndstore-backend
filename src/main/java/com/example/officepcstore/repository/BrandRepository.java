package com.example.officepcstore.repository;


import com.example.officepcstore.models.enity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  BrandRepository extends MongoRepository<Brand, String> {
    List<Brand> findAllByState(String state);
    Optional<Brand> findBrandByIdAndState(String id, String state);

    Page<Brand> findAllByState(String state, Pageable pageable);
    Page<Brand> findAll( Pageable pageable);

}
