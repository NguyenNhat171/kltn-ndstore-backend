package com.example.officepcstore.repository;

import com.example.officepcstore.models.enity.product.Product;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findProductByIdAndState(String id, String state);
    Page<Product> findAllByState(String state, Pageable pageable);

    Page<Product> findAll(Pageable pageable);
  // @Query( value= "{ $and:[{'price': {$gte: ?0 , $lte: ?1}}] ," + "  'state' : 'enable'}")
   Page <Product> findAllByPriceBetweenAndState( long priceMin,long PriceMax, String state,Pageable pageable);

 //   Page<Product> findAllByCategory_IdOrBrand_IdAndState(ObjectId catId, ObjectId brandId, String state, Pageable pageable);

//    @Query(value = "{ $or: [{'category' : {$in: ?0}},{'brand':{$in: ?1}}] ," +
//            "    'state' : 'enable'}")
    Page<Product> findAllBy(TextCriteria textCriteria, Pageable pageable);
//    @Query(value = "{ $or: [{'category' : ?0},{'category':{$in: ?1}}] ," +
//            "    'state' : 'enable'}")
    Page<Product> findAllByCategory_IdAndState(ObjectId id, String state ,Pageable pageable);
    Page<Product>findAllByBrand_IdAndState(ObjectId id, String state ,Pageable pageable);
    List<Product>findAllByBrand_IdAndState(ObjectId id,String state);
    List<Product>findAllByCategory_IdAndState(ObjectId id,String state);

  //  Page<Product> findByProductConfigurationMultipleKeyValuePairs(List<Map<String, String>> value, Pageable pageable);
}
