package com.example.officepcstore.repository;

import com.example.officepcstore.models.enity.product.Product;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    List<Product> findAllByState(String state);
    Page<Product> findAllByOrderBySoldAsc(Pageable pageable);
    Page<Product> findAllByOrderBySoldDesc(Pageable pageable);

    Page<Product> findAllByOrderByReducedPriceAsc(Pageable pageable);
    Page<Product> findAllByOrderByReducedPriceDesc(Pageable pageable);

    Page<Product> findAll(Pageable pageable);
  // @Query( value= "{ $and:[{'price': {$gte: ?0 , $lte: ?1}}] ," + "  'state' : 'enable'}")
   Page <Product> findAllByPriceBetweenAndState( long priceMin,long PriceMax, String state,Pageable pageable);
    @Query("{$and: [{$text: {$search: ?0}}, {'state': 'enable'}]}")
    List<Product> findAllByKeyword(String textCriteria);
 //   Page<Product> findAllByCategory_IdOrBrand_IdAndState(ObjectId catId, ObjectId brandId, String state, Pageable pageable);

//    @Query(value = "{ $or: [{'category' : {$in: ?0}},{'brand':{$in: ?1}}] ," +
//            "    'state' : 'enable'}")

Page<Product>findAllByNameLikeIgnoreCase(String name,Pageable pageable);
    Page<Product> findAllBy(TextCriteria textCriteria, Pageable pageable);
@Query("{$and: [{$text: {$search: ?0}}, {'state': 'enable'}]}")
    Page<Product> findAllByKeyword(String textCriteria, Pageable pageable);
//    @Query(value = "{ $or: [{'category' : ?0},{'category':{$in: ?1}}] ," +
//            "    'state' : 'enable'}")
    Page<Product> findAllByCategory_IdAndState(ObjectId id, String state ,Pageable pageable);
    Page<Product> findAllByCategory_Id(ObjectId id ,Pageable pageable);
    Page<Product>findAllByBrand_IdAndState(ObjectId id, String state ,Pageable pageable);
    Page<Product>findAllByBrand_Id(ObjectId id ,Pageable pageable);
    List<Product>findAllByBrand_IdAndState(ObjectId id,String state);
    List<Product>findAllByCategory_IdAndState(ObjectId id,String state);
    List<Product> findAllByCategory_IdAndBrand_IdAndState(ObjectId categoryId, ObjectId brandId, String state);

    Page<Product> findAllByCategory_IdAndBrand_IdAndState(ObjectId categoryId, ObjectId brandId, String state,Pageable pageable);

    Page<Product> findAllByCategory_IdAndBrand_IdAndNameLikeIgnoreCase(ObjectId categoryId, ObjectId brandId, String name,Pageable pageable);
    Page<Product> findAllByCategory_IdAndNameLikeIgnoreCase(ObjectId categoryId, String name,Pageable pageable);

    Page<Product> findAllByBrand_IdAndNameLikeIgnoreCase( ObjectId brandId, String name,Pageable pageable);
//    Page<Product> findAllByNameLike( String name,Pageable pageable);



//@Query(value="{'productConfiguration': { '$elemMatch': { '$and': ?0 } } }")
//   @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } }]," + "  'state' : 'enable'}")
//   Page<Product> findAllByProductConfiguration(List<Map<String, String>> queryParams,Pageable pageable);

    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 }]," + " 'state' : 'enable'}")
    Page<Product> findAllByProductConfigurationAndCategory_Id(List<Map<String, String>> queryParams,ObjectId categoryId,Pageable pageable);
    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 }]," + " 'state' : 'enable'}")
    List<Product> findAllByProductConfigurationAndCategory_Id(List<Map<String, String>> queryParams,ObjectId categoryId);

    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 },{ 'brand.id': ?2 }]," + " 'state' : 'enable'}")
    Page<Product> findAllByProductConfigurationAndCategory_IdAndBrand_Id(List<Map<String, String>> queryParams,ObjectId categoryId, ObjectId brandId,Pageable pageable);
    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 },{ 'brand.id': ?2 }]," + " 'state' : 'enable'}")
    List<Product> findAllByProductConfigurationAndCategory_IdAndBrand_Id(List<Map<String, String>> queryParams,ObjectId categoryId, ObjectId brandId);

//    @Query(value = "{ $and: ["
//            + "{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },"
//            + "{'category.id': ?1 },"
//            + "{'state': 'enable' },"
//            + "{'reducedPrice': { '$gte': ?2, '$lte': ?3 }}"
//            + "] }")
@Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 },{'reducedPrice': { '$gte': ?2, '$lte': ?3 }}]," + " 'state' : 'enable'}")
    Page<Product> findAllByProductConfigurationAndCategory_IdAndReducedPriceBetween(
            List<Map<String, String>> queryParams,
            ObjectId categoryId,
            long minPrice,
            long maxPrice,
            Pageable pageable);


    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 },{'reducedPrice': { '$gte': ?2, '$lte': ?3 }}]," + " 'state' : 'enable'}")
    List<Product> findAllByProductConfigurationAndCategory_IdAndReducedPriceBetween(
            List<Map<String, String>> queryParams,
            ObjectId categoryId,
            long minPrice,
            long maxPrice);


    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 },{ 'brand.id': ?2 },{'reducedPrice': { '$gte': ?3, '$lte': ?4 }}]," + " 'state' : 'enable'}")
    Page<Product> findAllByProductConfigurationAndCategory_IdAndBrand_IdAndReducedPriceBetween(
            List<Map<String, String>> queryParams,
            ObjectId categoryId,
            ObjectId brandId,
            long minPrice,
            long maxPrice,
            Pageable pageable);


    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 },{ 'brand.id': ?2 },{'reducedPrice': { '$gte': ?3, '$lte': ?4 }}]," + " 'state' : 'enable'}")
    List<Product> findAllByProductConfigurationAndCategory_IdAndBrand_IdAndReducedPriceBetween(
            List<Map<String, String>> queryParams,
            ObjectId categoryId,
            ObjectId brandId,
            long minPrice,
            long maxPrice);
//    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 },{'reducedPrice': { '$gte': ?2, '$lte': ?3 }}]," + " 'state' : 'enable'}")
//    Page<Product> findAllByProductConfigurationAndCategory_IdAndReducedPriceBetweenOrderByReducedPriceAsc(
//            List<Map<String, String>> queryParams,
//            ObjectId categoryId,
//            long minPrice,
//            long maxPrice,
//            Pageable pageable);



}
