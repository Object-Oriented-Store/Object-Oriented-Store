package com.ohgiraffers.store.product.controller;

import com.ohgiraffers.store.product.model.ProductDTO;
import com.ohgiraffers.store.product.service.ProductService;

import java.sql.SQLException;
import java.util.List;

/**
 * 화면 또는 메뉴의 요청을 상품 Service로 전달하는 진입점이다.
 *
 * 현재 프로젝트에는 콘솔 화면이 아직 없으므로 값을 직접 받아 Service에 넘긴다.
 * 나중에 ProductMenu를 만들면 메뉴가 이 Controller의 메서드를 호출하면 된다.
 */
public class ProductController {

    private final ProductService productService;

    public ProductController() {
        this.productService = new ProductService();
    }

    public List<ProductDTO> findAllProducts() throws SQLException {
        return productService.findAllProducts();
    }

    public ProductDTO findProductByCode(int productCode) throws SQLException {
        return productService.findProductByCode(productCode);
    }

    public List<ProductDTO> findProductsByCategory(int categoryCode) throws SQLException {
        return productService.findProductsByCategory(categoryCode);
    }

    public List<ProductDTO> searchProductsByName(String keyword) throws SQLException {
        return productService.searchProductsByName(keyword);
    }

    public ProductDTO validateProductPurchase(int productCode, int quantity)
            throws SQLException {
        return productService.validateProductPurchase(productCode, quantity);
    }

    public boolean registerProduct(ProductDTO product) throws SQLException {
        return productService.registerProduct(product);
    }

    public boolean updateProduct(ProductDTO product) throws SQLException {
        return productService.updateProduct(product);
    }

}
