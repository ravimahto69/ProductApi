import com.zest.productapi.dto.ProductRequest;
import com.zest.productapi.dto.ProductResponse;
import com.zest.productapi.entity.Product;
import com.zest.productapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(
            ProductRequest request,
            String username) {

        Product product = Product.builder()
                .productName(request.productName())
                .createdBy(username)
                .createdOn(LocalDateTime.now())
                .build();

        Product savedProduct =
                productRepository.save(product);

        return mapToResponse(savedProduct);
    }
    @Service
    @RequiredArgsConstructor
    public class ProductService {

        private final ProductRepository productRepository;

        public ProductResponse createProduct(
                ProductRequest request,
                String username) {

            Product product = Product.builder()
                    .productName(request.productName())
                    .createdBy(username)
                    .createdOn(LocalDateTime.now())
                    .build();

            Product savedProduct =
                    productRepository.save(product);

            return mapToResponse(savedProduct);
        }
    }
    private ProductResponse mapToResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .createdBy(product.getCreatedBy())
                .createdOn(product.getCreatedOn())
                .modifiedBy(product.getModifiedBy())
                .modifiedOn(product.getModifiedOn())
                .build();
    }
    public ProductResponse getProductById(Long id) {

        Product product = productRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        return mapToResponse(product);
    }
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request,
            String username) {

        Product product = productRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Product not found"
                        )
                );

        product.setProductName(
                request.productName()
        );

        product.setModifiedBy(username);

        product.setModifiedOn(
                LocalDateTime.now()
        );

        return mapToResponse(
                productRepository.save(product)
        );
    }
    public void deleteProduct(Long id) {

        Product product = productRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Product not found"
                        )
                );

        productRepository.delete(product);
    }
}