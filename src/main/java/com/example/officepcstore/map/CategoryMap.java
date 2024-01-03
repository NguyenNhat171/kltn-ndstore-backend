package com.example.officepcstore.map;


import com.example.officepcstore.models.enity.Category;
import com.example.officepcstore.payload.response.CategoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryMap {
    public CategoryResponse getCategoryResponse(Category category)
    {
        return
                new CategoryResponse(category.getId(),category.getTitleCategory(), category.getImageCategory(),category.getDisplay());
    }
}
