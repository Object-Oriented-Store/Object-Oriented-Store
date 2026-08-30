package com.ohgiraffers.store.order.view;

import com.ohgiraffers.store.category.controller.CategoryController;
import com.ohgiraffers.store.category.model.CategoryDTO;
import com.ohgiraffers.store.product.controller.ProductController;
import com.ohgiraffers.store.product.model.ProductDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class OrderPurchaseView {

    private final Scanner scanner;
    private final ProductController productController;
    private final CategoryController categoryController;
    private final OrderView orderView;

    public OrderPurchaseView(
            Scanner scanner
    ) {

        if (scanner == null) {
            throw new IllegalArgumentException(
                    "입력 도구가 필요합니다."
            );
        }

        this.scanner = scanner;
        this.productController =
                new ProductController();
        this.categoryController =
                new CategoryController();
        this.orderView =
                new OrderView(scanner);
    }

    // 상품 화면에서 선택을 마친 상품을 받아 실제 주문에 담는다.
    public int run(
            int memberCode,
            ProductDTO selectedProduct
    ) {

        if (memberCode <= 0) {
            System.out.println(
                    "올바른 회원정보가 필요합니다."
            );
            return OrderView.EXIT_PURCHASE;
        }

        if (selectedProduct == null) {
            System.out.println(
                    "선택된 상품정보가 없습니다."
            );
            return OrderView.ADD_MORE_PRODUCT;
        }

        int orderResult =
                orderView.addSelectedProduct(
                        memberCode,
                        selectedProduct.getProductCode(),
                        selectedProduct.getProductName(),
                        selectedProduct.getProductPrice(),
                        selectedProduct.getStockQuantity()
                );

        // 상품 담기가 끝났으면 주문 담당의 장바구니 화면으로 이동한다.
        if (orderResult == OrderView.MOVE_TO_CART) {
            return orderView.run(memberCode);
        }

        return orderResult;
    }

    // 상품 선택부터 장바구니 이동까지 실행
    public int run(
            int memberCode
    ) {

        if (memberCode <= 0) {
            System.out.println(
                    "올바른 회원정보가 필요합니다."
            );
            return OrderView.EXIT_PURCHASE;
        }

        while (true) {
            try {
                ProductDTO selectedProduct =
                        selectProductByCategory();

                if (selectedProduct == null) {
                    System.out.println(
                            "상품 구매 화면을 종료합니다."
                    );
                    return OrderView.EXIT_PURCHASE;
                }

                int orderResult =
                        run(memberCode, selectedProduct);

                // 상품을 더 추가하는 경우 카테고리부터 다시 선택
                if (orderResult
                        == OrderView.ADD_MORE_PRODUCT) {

                    continue;
                }

                return orderResult;

            } catch (SQLException e) {
                System.out.println(
                        "상품 조회 중 DB 오류가 발생했습니다: "
                                + e.getMessage()
                );

                return OrderView.EXIT_PURCHASE;

            } catch (RuntimeException e) {
                System.out.println(
                        "구매 처리 중 오류가 발생했습니다: "
                                + e.getMessage()
                );
            }
        }
    }

    // 카테고리를 조회하고 상품 한 개 선택
    private ProductDTO selectProductByCategory()
            throws SQLException {

        while (true) {
            printCategories();

            int categoryCode =
                    readInt(
                            "카테고리를 선택하세요 "
                                    + "(이전 화면: 0): "
                    );

            if (categoryCode == 0) {
                return null;
            }

            List<ProductDTO> products =
                    productController.findProductsByCategory(
                            categoryCode
                    );

            if (products.isEmpty()) {
                System.out.println(
                        "해당 카테고리에 상품이 없습니다."
                );
                continue;
            }

            printProducts(products);

            int productCode =
                    readInt(
                            "구매할 상품코드를 입력하세요 "
                                    + "(카테고리 재선택: 0): "
                    );

            if (productCode == 0) {
                continue;
            }

            ProductDTO selectedProduct =
                    findProduct(
                            products,
                            productCode
                    );

            if (selectedProduct == null) {
                System.out.println(
                        "현재 상품 목록에 없는 상품코드입니다."
                );
                continue;
            }

            return selectedProduct;
        }
    }

    // 카테고리 목록 출력
    private void printCategories()
            throws SQLException {

        List<CategoryDTO> categories =
                categoryController.findAllCategories();

        System.out.println();
        System.out.println("========== 카테고리 ==========");

        for (CategoryDTO category : categories) {
            System.out.printf(
                    "%d. %s%n",
                    category.getCategoryCode(),
                    category.getCategoryName()
            );
        }

        System.out.println("0. 이전 화면");
        System.out.println("=============================");
    }

    // 선택한 카테고리의 상품 목록 출력
    private void printProducts(
            List<ProductDTO> products
    ) {

        System.out.println();
        System.out.println(
                "================ 상품 목록 ================"
        );

        for (ProductDTO product : products) {
            System.out.printf(
                    "상품코드: %d | 상품명: %s | "
                            + "가격: %,d원 | 재고: %d개%n",
                    product.getProductCode(),
                    product.getProductName(),
                    product.getProductPrice(),
                    product.getStockQuantity()
            );
        }

        System.out.println(
                "=========================================="
        );
    }

    // 현재 출력된 상품에서 상품코드 찾기
    private ProductDTO findProduct(
            List<ProductDTO> products,
            int productCode
    ) {

        for (ProductDTO product : products) {
            if (product.getProductCode()
                    == productCode) {

                return product;
            }
        }

        return null;
    }

    // 숫자 입력 처리
    private int readInt(
            String message
    ) {

        while (true) {
            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println(
                        "숫자로 입력해주세요."
                );
            }
        }
    }
}
