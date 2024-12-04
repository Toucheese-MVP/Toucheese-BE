package com.toucheese.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toucheese.global.exception.ToucheeseBadRequestException;
import com.toucheese.product.dto.ProductDetailResponse;
import com.toucheese.product.entity.Product;
import com.toucheese.product.entity.ProductAddOption;
import com.toucheese.product.repository.ProductAddOptionRepository;
import com.toucheese.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductAddOptionRepository productAddOptionRepository;

    /**
     * 상품 상세 정보를 조회한다.
     * @param productId 상품 아이디
     * @return 아이디에 해당하는 상품 상세 정보
     * @throws ToucheeseBadRequestException 존재하지 않는 상품에 접근할 때 발생
     */
    @Transactional(readOnly = true)
    public ProductDetailResponse findProductDetailById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ToucheeseBadRequestException::new);

        return ProductDetailResponse.of(product);
    }

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new ToucheeseBadRequestException("Product not found"));
    }

    /**
     * 상품에 해당하는 AddOption을 조회한다.
     * @param addOptionId AddOption 아이디
     * @return 해당하는 AddOption 객체
     * @throws ToucheeseBadRequestException 존재하지 않는 AddOption에 접근할 때 발생
     */
    public ProductAddOption findProductAddOptionById(Long addOptionId) {
        return productAddOptionRepository.findById(addOptionId)
            .orElseThrow(() -> new ToucheeseBadRequestException("AddOption not found"));
    }
}
