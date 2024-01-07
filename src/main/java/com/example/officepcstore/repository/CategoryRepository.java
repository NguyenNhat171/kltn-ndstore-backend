package com.example.officepcstore.repository;

import com.example.officepcstore.models.enity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository  extends MongoRepository<Category, String> {
    List<Category> findAllByDisplay(String state);
   Page<Category>findAllByDisplay(String state, Pageable pageable);

    Page<Category>findAllByTitleCategoryLikeIgnoreCase(String name, Pageable pageable);
   Page<Category>findAllByTitleCategoryLikeIgnoreCaseAndDisplay(String name, String state,Pageable pageable);
    Optional<Category> findCategoryByIdAndDisplay(String id, String state);

}
