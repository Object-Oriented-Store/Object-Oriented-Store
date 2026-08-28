package com.ohgiraffers.store.product.model;

/**
 * 상품 한 건의 데이터를 담아 계층 사이로 전달하는 객체이다.
 *
 * DTO(Data Transfer Object)는 SQL을 실행하거나 업무를 처리하지 않는다.
 * tbl_product의 한 행을 Java 객체로 옮겨 담는 데이터 상자 역할만 한다.
 */
public class ProductDTO {

    /* 각 필드는 tbl_product의 입력 가능한 컬럼과 대응한다. */
    private int productCode;
    private String productName;
    private int productPrice;
    private String productStatus;
    private int stockQuantity;
    private int categoryCode;

    /* 프레임워크나 빈 객체가 필요할 때 사용하는 기본 생성자이다. */
    public ProductDTO() {
    }

    /*
     * 신규 등록용 생성자이다.
     * product_code는 AUTO_INCREMENT이므로 DB가 생성해 주어 여기서는 받지 않는다.
     */
    public ProductDTO(
            String productName,
            int productPrice,
            int stockQuantity,
            int categoryCode
    ) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.stockQuantity = stockQuantity;
        this.categoryCode = categoryCode;
    }

    /* 기존 상품 수정용이며 상태는 수정 후 조회할 때 XML 쿼리가 계산한다. */
    public ProductDTO(
            int productCode,
            String productName,
            int productPrice,
            int stockQuantity,
            int categoryCode
    ) {
        this(
                productCode,
                productName,
                productPrice,
                null,
                stockQuantity,
                categoryCode
        );
    }

    /* DB에서 조회한 기존 상품의 모든 값을 담을 때 사용하는 생성자이다. */
    public ProductDTO(
            int productCode,
            String productName,
            int productPrice,
            String productStatus,
            int stockQuantity,
            int categoryCode
    ) {
        this.productCode = productCode;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productStatus = productStatus;
        this.stockQuantity = stockQuantity;
        this.categoryCode = categoryCode;
    }

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(int categoryCode) {
        this.categoryCode = categoryCode;
    }

    /* 객체 안의 값을 콘솔에서 한 번에 확인하기 위한 문자열 표현이다. */
    @Override
    public String toString() {
        return "ProductDTO{" +
                "productCode=" + productCode +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", productStatus='" + productStatus + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", categoryCode=" + categoryCode +
                '}';
    }

}
