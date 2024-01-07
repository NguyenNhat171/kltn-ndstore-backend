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

   Page <Product> findAllByPriceBetweenAndState( long priceMin,long PriceMax, String state,Pageable pageable);

    @Query("{$and: [{'name': {$regex: ?0}}, {'state': 'enable'}]}")
    List<Product> findAllByKeyword(String textCriteria);

Page<Product>findAllByNameLikeIgnoreCase(String name,Pageable pageable);
    Page<Product> findAllBy(TextCriteria textCriteria, Pageable pageable);
@Query("{$and: [{'name': {$regex: ?0}}, {'state': 'enable'}]}")

    Page<Product> findAllByKeyword(String textCriteria, Pageable pageable);

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





    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 }]," + " 'state' : 'enable'}")
    Page<Product> findAllByProductConfigurationAndCategory_Id(List<Map<String, String>> queryParams,ObjectId categoryId,Pageable pageable);
    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 }]," + " 'state' : 'enable'}")
    List<Product> findAllByProductConfigurationAndCategory_Id(List<Map<String, String>> queryParams,ObjectId categoryId);

    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 },{ 'brand.id': ?2 }]," + " 'state' : 'enable'}")
    Page<Product> findAllByProductConfigurationAndCategory_IdAndBrand_Id(List<Map<String, String>> queryParams,ObjectId categoryId, ObjectId brandId,Pageable pageable);
    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'category.id': ?1 },{ 'brand.id': ?2 }]," + " 'state' : 'enable'}")
    List<Product> findAllByProductConfigurationAndCategory_IdAndBrand_Id(List<Map<String, String>> queryParams,ObjectId categoryId, ObjectId brandId);


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


   // @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'brand.id': ?1 },{'reducedPrice': { '$gte': ?2, '$lte': ?3 }},{$text: {$search: ?4}}]," + " 'state' : 'enable'}")
   @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'brand.id': ?1 },{'reducedPrice': { '$gte': ?2, '$lte': ?3 }},{'name': {$regex: ?4}}]," + " 'state' : 'enable'}")
    List<Product> findAllByProductConfigurationAndBrand_IdAndReducedPriceBetweenAndKeyword(
            List<Map<String, String>> queryParams,
            ObjectId brandId,
            long minPrice,
            long maxPrice,
              String keyword
    );

  //  @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'brand.id': ?1 },{'reducedPrice': { '$gte': ?2, '$lte': ?3 }},{$text: {$search: ?4}}]," + " 'state' : 'enable'}")
  @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{ 'brand.id': ?1 },{'reducedPrice': { '$gte': ?2, '$lte': ?3 }},{'name': {$regex: ?4}}]," + " 'state' : 'enable'}")
    Page<Product> findAllByProductConfigurationAndBrand_IdAndReducedPriceBetweenAndKeyword(
            List<Map<String, String>> queryParams,
            ObjectId brandId,
            long minPrice,
            long maxPrice,
            String keyword,
            Pageable page
    );

    //@Query(value="{ $and:[{'reducedPrice': { '$gte': ?0, '$lte': ?1 }},{$text: {$search: ?2}}]," + " 'state' : 'enable'}")
    @Query(value="{ $and:[{'reducedPrice': { '$gte': ?0, '$lte': ?1 }},{'name': {$regex: ?2}}]," + " 'state' : 'enable'}")
    List<Product> findAllByPriceBetweenAndKeyword(
            long minPrice,
            long maxPrice,
            String keyword
    );

   // @Query(value="{ $and:[{'reducedPrice': { '$gte': ?0, '$lte': ?1 }},{$text: {$search: ?2}}]," + " 'state' : 'enable'}")
   @Query(value="{ $and:[{'reducedPrice': { '$gte': ?0, '$lte': ?1 }},{'name': {$regex: ?2}}]," + " 'state' : 'enable'}")
    Page<Product> findAllByPriceBetweenAndKeyword(
            long minPrice,
            long maxPrice,
            String keyword,
            Pageable pageable
    );

      @Query(value="{ $and:[{ 'brand.id': ?0 },{'reducedPrice': { '$gte': ?1, '$lte': ?2 }},{'name': {$regex: ?3}}]," + " 'state' : 'enable'}")
  //  @Query(value="{ $and:[{ 'brand.id': ?0 },{'reducedPrice': { '$gte': ?1, '$lte': ?2 }},{$text: {$search: ?3}}]," + " 'state' : 'enable'}")
    List<Product> findAllByBrand_IdAndReducedPriceBetweenAndKeyword(
            ObjectId brandId,
            long minPrice,
            long maxPrice,
            String keyword
    );
    @Query(value="{ $and:[{ 'brand.id': ?0 },{'reducedPrice': { '$gte': ?1, '$lte': ?2 }},{'name': {$regex: ?3}}]," + " 'state' : 'enable'}")
    //@Query(value="{ $and:[{ 'brand.id': ?0 },{'reducedPrice': { '$gte': ?1, '$lte': ?2 }},{$text: {$search: ?3}}]," + " 'state' : 'enable'}")
    Page<Product> findAllByBrand_IdAndReducedPriceBetweenAndKeyword(
            ObjectId brandId,
            long minPrice,
            long maxPrice,
            String keyword,
            Pageable pageable
    );
    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{'reducedPrice': { '$gte': ?1, '$lte': ?2 }},{'name': {$$regex: ?3}}]," + " 'state' : 'enable'}")
  //  @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{'reducedPrice': { '$gte': ?1, '$lte': ?2 }},{$text: {$search: ?3}}]," + " 'state' : 'enable'}")
    List<Product> findAllByProductConfigurationAndReducedPriceBetweenAndKeyword(
            List<Map<String, String>> queryParams,
            long minPrice,
            long maxPrice,
            String keyword
    );

//  @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{'reducedPrice': { '$gte': ?1, '$lte': ?2 }},{$text: {$search: ?3}}]," + " 'state' : 'enable'}")
@Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{'reducedPrice': { '$gte': ?1, '$lte': ?2 }},{'name': {$regex: ?3}}]," + " 'state' : 'enable'}")
    Page<Product> findAllByProductConfigurationAndReducedPriceBetweenAndKeyword(
            List<Map<String, String>> queryParams,
            long minPrice,
            long maxPrice,
            String keyword,
            Pageable p
    );


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


    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{'name': {$regex: ?1}}]," + " 'state' : 'enable'}")
    List<Product> findAllByProductConfigurationAndKeyword(
            List<Map<String, String>> queryParams,
            String keyword
    );
    @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{'name': {$regex: ?1}}]," + " 'state' : 'enable'}")
   // @Query(value="{ $and:[{'productConfiguration': { '$elemMatch': { '$and': ?0 } } },{$text: {$search: ?1}}]," + " 'state' : 'enable'}")
    Page<Product> findAllByProductConfigurationAndKeyword(
            List<Map<String, String>> queryParams,
            String keyword,
            Pageable pageable
    );

   @Query("{$and: [{'name': {$regex: ?0}},{ 'brand.id': ?1 }, {'state': 'enable'}]}")
    List<Product> findAllByKeywordAndBrand_Id( String keyword,ObjectId brandId);
    @Query("{$and: [{'name': {$regex: ?0}},{ 'brand.id': ?1 }, {'state': 'enable'}]}")

    Page<Product> findAllByKeywordAndBrand_Id( String keyword,ObjectId brandId,Pageable pageable);




}
