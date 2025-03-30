package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Converter.DTOConverter;
import com.asss.www.ApotekarskaUstanova.Repository.ProductBatchRepository;
import com.asss.www.ApotekarskaUstanova.Dto.ProductDto;
import com.asss.www.ApotekarskaUstanova.Entity.Product;
import com.asss.www.ApotekarskaUstanova.Repository.ProductRepository;
import com.asss.www.ApotekarskaUstanova.Entity.ProductBatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductBatchRepository productBatchRepository;

    /**
     * Dohvata sve proizvode iz baze podataka.
     * @return lista proizvoda.
     */
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(DTOConverter::toProductDTO) // Use DTOConverter to map Product to ProductDto
                .collect(Collectors.toList());
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Proizvod sa ID " + productId + " nije pronađen."));
    }

    public List<Integer> getProductDosages(String name) {
        return productRepository.findDosagesByName(name);
    }

    public Optional<Product> findProductByEan13(String ean13) {
        List<ProductBatch> productBatches = productBatchRepository.findByEan13(ean13);

        if (productBatches.isEmpty()) {
            return Optional.empty();
        }

        // Pretpostavljamo da uzimamo prvi batch i njegov product id
        ProductBatch productBatch = productBatches.get(0);  // Ovaj kod može biti proširen za dodatnu logiku

        return productRepository.findById(productBatch.getProduct().getId());
    }

    public int getProductIdByName(String name) {
        Optional<Product> product = productRepository.findByName(name);
        return product.map(Product::getId).orElse(null);
    }


    public List<ProductDto> findByName(String query) {
        return productRepository.findByProductName(query).stream()
                .map(DTOConverter::toProductDTO) // Poziv statičke metode
                .collect(Collectors.toList());
    }

    public List<ProductDto> getLowStockProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDto> lowStockProducts = new ArrayList<>();

        for (Product product : products) {
            int totalRemainingQuantity = productBatchRepository.sumRemainingQuantityByProductId(product.getId());

            if (totalRemainingQuantity < product.getMinQuantity()) {
                ProductDto productDto = new ProductDto(product);
                productDto.setStockQuantity(totalRemainingQuantity);
                productDto.setMinQuantity(product.getMinQuantity());
                lowStockProducts.add(productDto);
            }
        }

        return lowStockProducts;
    }
}
