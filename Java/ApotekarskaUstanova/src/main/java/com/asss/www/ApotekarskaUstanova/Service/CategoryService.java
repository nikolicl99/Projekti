package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dto.CategoryDto;
import com.asss.www.ApotekarskaUstanova.Entity.Category;
import com.asss.www.ApotekarskaUstanova.Dao.CategoryRepository;
import com.asss.www.ApotekarskaUstanova.Converter.DTOConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(DTOConverter::toCategoryDTO)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return DTOConverter.toCategoryDTO(category);
    }
}
