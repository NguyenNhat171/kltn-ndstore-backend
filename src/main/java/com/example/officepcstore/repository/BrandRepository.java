package com.example.officepcstore.repository;


import com.example.officepcstore.models.enity.Brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  BrandRepository extends MongoRepository<Brand, String> {
    List<Brand> findAllByDisplay(String state);
    Optional<Brand> findBrandByIdAndDisplay(String id, String state);
  //@Query("{$and: [{$text: {$search: ?0}}, {'state': 'enable'}]}")
//  @Query("{$text: {$search: ?0}}")
//    Page<Brand> getAllBrandByNameSort(String nameBrand,Pageable pageable);
    Page<Brand> findAllByName(String name, Pageable pageable);
    Page<Brand> findAllByNameLikeIgnoreCase(String name, Pageable pageable);
    Page<Brand> findAllByDisplay(String state, Pageable pageable);
    Page<Brand> findAllByDisplayAndNameLikeIgnoreCase(String state, String name, Pageable pageable);
    Page<Brand> findAll( Pageable pageable);

}
