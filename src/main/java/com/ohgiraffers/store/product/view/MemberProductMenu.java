package com.ohgiraffers.store.product.view;

import com.ohgiraffers.store.category.controller.CategoryController;
import com.ohgiraffers.store.category.model.CategoryDTO;
import com.ohgiraffers.store.product.controller.ProductController;
import com.ohgiraffers.store.product.model.ProductDTO;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    /* 실제 주문 기능이 연결되기 전까지 선택한 상품을 메모리에 보관한다. */
    private final Map<Integer, CartItem> cart;

    public MemberProductMenu() {
        this.scanner = new Scanner(System.in);
        this.productController = new ProductController();
        this.categoryController = new CategoryController();
        this.cart = new LinkedHashMap<>();
    }

    /** 로그인 기능이 완성되기 전 이 화면만 직접 실행할 수 있는 시작점이다. */
    public static void main(String[] args) {
        new MemberProductMenu().run();
    }

    public void run() {
        while (true) {
            printMainMenu();
            int menuNumber = readInt("메뉴를 선택하세요: ");

            try {
                switch (menuNumber) {
                    case 1 -> showAllProducts();
                    case 2 -> showProductsByCategory();
                    case 3 -> searchProductsByName();
                    case 0 -> {
                        System.out.println("상품 조회를 종료합니다.");
                        return;
                    }
                    default -> System.out.println("목록에 있는 메뉴 번호를 입력하세요.");
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

    private void showAllProducts() throws SQLException {
        List<ProductDTO> products = productController.findAllProducts();
        printProducts(products);
        offerPurchase(products);
    }

    private void showProductsByCategory() throws SQLException {
        printCategories();
        int categoryCode = readInt("조회할 카테고리코드: ");
        List<ProductDTO> products = productController.findProductsByCategory(categoryCode);
        printProducts(products);
        offerPurchase(products);
    }

    private void searchProductsByName() throws SQLException {
        String keyword = readText("검색할 상품명: ");
        List<ProductDTO> products = productController.searchProductsByName(keyword);
        printProducts(products);
        offerPurchase(products);
    }

    /** 조회 결과에서 상품 하나를 골라 임시 장바구니에 담는다. */
    private void offerPurchase(List<ProductDTO> products) throws SQLException {
        if (products.isEmpty()) {
            return;
        }

        int productCode = readInt(
                "바로 구매할 상품코드를 입력하세요 (구매하지 않으려면 0): "
        );

        if (productCode == 0) {
            System.out.println("상품을 구매하지 않고 메뉴로 돌아갑니다.");
            return;
        }

        ProductDTO selectedProduct = findProduct(products, productCode);

        if (selectedProduct == null) {
            System.out.println("현재 조회 결과에 없는 상품코드입니다.");
            return;
        }

        int quantity = readInt("구매수량: ");
        CartItem currentItem = cart.get(productCode);
        int totalQuantity = quantity;

        /* 이미 담긴 상품이면 기존 수량과 새 수량을 합쳐 재고를 검사한다. */
        if (currentItem != null) {
            totalQuantity += currentItem.quantity;
        }

        try {
            ProductDTO validatedProduct = productController.validateProductPurchase(
                    productCode,
                    totalQuantity
            );

            cart.put(productCode, new CartItem(validatedProduct, totalQuantity));
            System.out.println(validatedProduct.getProductName()
                    + " " + quantity + "개를 장바구니에 담았습니다.");
        } catch (IllegalStateException exception) {
            System.out.println("구매 실패: " + exception.getMessage());
            return;
        }

        if (readYesOrNo("더 구매하시겠습니까? (Y/N): ")) {
            System.out.println("상품을 더 조회한 후 장바구니에 추가하세요.");
            return;
        }

        checkout();
    }

    private ProductDTO findProduct(List<ProductDTO> products, int productCode) {
        for (ProductDTO product : products) {
            if (product.getProductCode() == productCode) {
                return product;
            }
        }

        return null;
    }

    /** 장바구니를 출력하고 모든 상품을 다시 검사한 뒤 최종 구매 여부를 확인한다. */
    private void checkout() throws SQLException {
        if (cart.isEmpty()) {
            System.out.println("장바구니가 비어 있습니다.");
            return;
        }

        printCart();

        if (!readYesOrNo("최종 구매하시겠습니까? (Y/N): ")) {
            System.out.println("구매를 보류했습니다. 장바구니는 유지됩니다.");
            return;
        }

        long totalAmount = 0;

        try {
            /* 장바구니에 담은 뒤 상태나 재고가 바뀌었을 수 있어 마지막으로 다시 검사한다. */
            for (CartItem cartItem : cart.values()) {
                ProductDTO currentProduct = productController.validateProductPurchase(
                        cartItem.product.getProductCode(),
                        cartItem.quantity
                );
                cartItem.product = currentProduct;
                totalAmount += (long) currentProduct.getProductPrice() * cartItem.quantity;
            }
        } catch (IllegalStateException exception) {
            System.out.println("최종 구매 실패: " + exception.getMessage());
            return;
        }

        System.out.println("최종 구매 요청 준비 완료");
        System.out.println("상품 총액: " + totalAmount + "원");
        System.out.println("※ OrderController 연결 후 주문과 재고 감소가 DB에 반영됩니다.");
    }

    private void printCart() {
        System.out.println("[장바구니]");
        long totalAmount = 0;

        for (CartItem cartItem : cart.values()) {
            long itemAmount = (long) cartItem.product.getProductPrice() * cartItem.quantity;
            totalAmount += itemAmount;

            System.out.printf(
                    "%s | %d개 | %d원%n",
                    cartItem.product.getProductName(),
                    cartItem.quantity,
                    itemAmount
            );
        }

        System.out.println("예상 상품 총액: " + totalAmount + "원");
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

    private boolean readYesOrNo(String message) {
        while (true) {
            String answer = readText(message).trim();

            if ("Y".equalsIgnoreCase(answer)) {
                return true;
            }

            if ("N".equalsIgnoreCase(answer)) {
                return false;
            }

            System.out.println("Y 또는 N으로 입력하세요.");
        }
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

    /** 실제 주문 기능이 연결되기 전까지 화면에서만 유지하는 장바구니 항목이다. */
    private static final class CartItem {

        private ProductDTO product;
        private final int quantity;

        private CartItem(ProductDTO product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }
}
