package com.asss.www.ApotekarskaUstanova.Converter;

import com.asss.www.ApotekarskaUstanova.Dto.CategoryDto;
import com.asss.www.ApotekarskaUstanova.Dto.ProductDto;
import com.asss.www.ApotekarskaUstanova.Dto.SubcategoryDto;
import com.asss.www.ApotekarskaUstanova.Entity.Category;
import com.asss.www.ApotekarskaUstanova.Entity.Product;
import com.asss.www.ApotekarskaUstanova.Entity.Subcategory;

import java.util.List;
import java.util.stream.Collectors;

public class DTOConverter {

    public static CategoryDto toCategoryDTO(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());

        // Konvertovanje liste podkategorija
        List<SubcategoryDto> subcategories = category.getSubcategories()
                .stream()
                .map(DTOConverter::toSubcategoryDTO)
                .collect(Collectors.toList());

        dto.setSubcategories(subcategories);
        return dto;
    }

    public static SubcategoryDto toSubcategoryDTO(Subcategory subcategory) {
        SubcategoryDto dto = new SubcategoryDto();
        dto.setId(subcategory.getId());
        dto.setName(subcategory.getName());
        dto.setDescription(subcategory.getDescription());
        return dto;
    }
    public static ProductDto toProductDTO(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPurchasePrice(product.getPurchasePrice());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setStockQuantity(product.getStockQuantity());
        return dto;
    }


}
