package com.ohgiraffers.store.product.view;

import com.ohgiraffers.store.category.controller.CategoryController;
import com.ohgiraffers.store.category.model.CategoryDTO;
import com.ohgiraffers.store.order.view.OrderPurchaseView;
import com.ohgiraffers.store.order.view.OrderView;
import com.ohgiraffers.store.product.controller.ProductController;
import com.ohgiraffers.store.product.model.ProductDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * 일반회원에게 보여줄 상품 메뉴이다.
 *
 * 일반회원은 상품을 조회하고 조회 결과에서 구매할 상품을 장바구니에 담는다.
 * 상품 등록과 수정 기능에는 접근할 수 없다.
 */
public class MemberProductMenu {

    private final Scanner scanner;
    private final ProductController productController;
    private final CategoryController categoryController;
    private final OrderPurchaseView orderPurchaseView;

    public MemberProductMenu(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("입력 도구가 필요합니다.");
        }

        this.scanner = scanner;
        this.productController = new ProductController();
        this.categoryController = new CategoryController();
        this.orderPurchaseView = new OrderPurchaseView(scanner);
    }

    /** 로그인 회원이 상품을 조회하고 선택한 뒤 주문 화면으로 이동한다. */
    public int run(int memberCode) {
        if (memberCode <= 0) {
            System.out.println("올바른 회원정보가 필요합니다.");
            return OrderView.EXIT_PURCHASE;
        }

        while (true) {
            printMainMenu();
            int menuNumber = readInt("메뉴를 선택하세요: ");

            try {
                int purchaseResult;

                switch (menuNumber) {
                    case 1 -> purchaseResult = showAllProducts(memberCode);
                    case 2 -> purchaseResult = showProductsByCategory(memberCode);
                    case 3 -> purchaseResult = searchProductsByName(memberCode);
                    case 0 -> {
                        System.out.println("상품 조회를 종료합니다.");
                        return OrderView.EXIT_PURCHASE;
                    }
                    default -> {
                        System.out.println("목록에 있는 메뉴 번호를 입력하세요.");
                        continue;
                    }
                }

                if (purchaseResult == OrderView.REQUEST_PAYMENT) {
                    return OrderView.REQUEST_PAYMENT;
                }

                if (purchaseResult == OrderView.EXIT_PURCHASE) {
                    return OrderView.EXIT_PURCHASE;
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("입력 오류: " + exception.getMessage());
            } catch (SQLException exception) {
                System.out.println("DB 처리 중 오류가 발생했습니다: " + exception.getMessage());
            }

            System.out.println();
        }
    }

    private void printMainMenu() {
        System.out.println("========================================");
        System.out.println("          멤버십 상품 화면");
        System.out.println("========================================");
        System.out.println("1. 상품 전체 조회");
        System.out.println("2. 카테고리별 상품 조회");
        System.out.println("3. 상품명 검색");
        System.out.println("0. 돌아가기");
        System.out.println("========================================");
    }

    private int showAllProducts(int memberCode) throws SQLException {
        List<ProductDTO> products = productController.findAllProducts();
        printProducts(products);
        return offerPurchase(memberCode, products);
    }

    private int showProductsByCategory(int memberCode) throws SQLException {
        printCategories();
        int categoryCode = readInt("조회할 카테고리코드: ");
        List<ProductDTO> products = productController.findProductsByCategory(categoryCode);
        printProducts(products);
        return offerPurchase(memberCode, products);
    }

    private int searchProductsByName(int memberCode) throws SQLException {
        String keyword = readText("검색할 상품명: ");
        List<ProductDTO> products = productController.searchProductsByName(keyword);
        printProducts(products);
        return offerPurchase(memberCode, products);
    }

    /** 조회 결과에서 상품을 고른 뒤 실제 장바구니 처리는 주문 담당 코드에 맡긴다. */
    private int offerPurchase(int memberCode, List<ProductDTO> products) {
        if (products.isEmpty()) {
            return OrderView.ADD_MORE_PRODUCT;
        }

        int productCode = readInt(
                "바로 구매할 상품코드를 입력하세요 (구매하지 않으려면 0): "
        );

        if (productCode == 0) {
            System.out.println("상품을 구매하지 않고 메뉴로 돌아갑니다.");
            return OrderView.ADD_MORE_PRODUCT;
        }

        ProductDTO selectedProduct = findProduct(products, productCode);

        if (selectedProduct == null) {
            System.out.println("현재 조회 결과에 없는 상품코드입니다.");
            return OrderView.ADD_MORE_PRODUCT;
        }

        return orderPurchaseView.run(memberCode, selectedProduct);
    }

    private ProductDTO findProduct(List<ProductDTO> products, int productCode) {
        for (ProductDTO product : products) {
            if (product.getProductCode() == productCode) {
                return product;
            }
        }

        return null;
    }

    private void printCategories() throws SQLException {
        List<CategoryDTO> categories = categoryController.findAllCategories();

        System.out.println("[카테고리 목록]");
        for (CategoryDTO category : categories) {
            System.out.printf(
                    "%d. %s%n",
                    category.getCategoryCode(),
                    category.getCategoryName()
            );
        }
    }

    private int readInt(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("숫자로 입력하세요.");
            }
        }
    }

    private String readText(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private void printProducts(List<ProductDTO> products) {
        if (products.isEmpty()) {
            System.out.println("조회된 상품이 없습니다.");
            return;
        }

        String border = "=".repeat(80);
        String separator = "-".repeat(80);

        System.out.println(border);
        System.out.println(
                padLeft("코드", 4) + "  "
                        + padRight("상품명", 28) + "  "
                        + padLeft("가격", 12) + "  "
                        + padRight("상태", 8) + "  "
                        + padLeft("재고", 8) + "  "
                        + padLeft("카테고리", 10)
        );
        System.out.println(separator);

        for (ProductDTO product : products) {
            printProduct(product);
        }

        System.out.println(border);
        System.out.printf("총 %,d개의 상품이 조회되었습니다.%n", products.size());
    }

    private void printProduct(ProductDTO product) {
        String price = String.format("%,d원", product.getProductPrice());
        String status = "Y".equalsIgnoreCase(product.getProductStatus())
                ? "판매중"
                : "품절";
        String stock = String.format("%,d개", product.getStockQuantity());

        System.out.println(
                padLeft(String.valueOf(product.getProductCode()), 4) + "  "
                        + padRight(product.getProductName(), 28) + "  "
                        + padLeft(price, 12) + "  "
                        + padRight(status, 8) + "  "
                        + padLeft(stock, 8) + "  "
                        + padLeft(String.valueOf(product.getCategoryCode()), 10)
        );
    }

    /** 한글처럼 콘솔에서 두 칸을 차지하는 글자를 고려해 오른쪽을 공백으로 채운다. */
    private String padRight(String value, int targetWidth) {
        int padding = Math.max(0, targetWidth - displayWidth(value));
        return value + " ".repeat(padding);
    }

    /** 한글처럼 콘솔에서 두 칸을 차지하는 글자를 고려해 왼쪽을 공백으로 채운다. */
    private String padLeft(String value, int targetWidth) {
        int padding = Math.max(0, targetWidth - displayWidth(value));
        return " ".repeat(padding) + value;
    }

    /** 현재 프로젝트에서 사용하는 한글·한자·전각문자는 두 칸, 나머지는 한 칸으로 계산한다. */
    private int displayWidth(String value) {
        int width = 0;

        for (int index = 0; index < value.length(); ) {
            int codePoint = value.codePointAt(index);
            width += isWideCharacter(codePoint) ? 2 : 1;
            index += Character.charCount(codePoint);
        }

        return width;
    }

    private boolean isWideCharacter(int codePoint) {
        return (codePoint >= 0x1100 && codePoint <= 0x115F)
                || (codePoint >= 0x2E80 && codePoint <= 0xA4CF)
                || (codePoint >= 0xAC00 && codePoint <= 0xD7A3)
                || (codePoint >= 0xF900 && codePoint <= 0xFAFF)
                || (codePoint >= 0xFE10 && codePoint <= 0xFE19)
                || (codePoint >= 0xFF01 && codePoint <= 0xFF60)
                || (codePoint >= 0xFFE0 && codePoint <= 0xFFE6);
    }

}
