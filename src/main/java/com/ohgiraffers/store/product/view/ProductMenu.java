package com.ohgiraffers.store.product.view;

import com.ohgiraffers.store.category.controller.CategoryController;
import com.ohgiraffers.store.category.model.CategoryDTO;
import com.ohgiraffers.store.product.controller.ProductController;
import com.ohgiraffers.store.product.model.ProductDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * 콘솔에서 상품 기능을 선택하고 필요한 값을 입력받는 화면 계층이다.
 *
 * Menu는 SQL이나 트랜잭션을 처리하지 않는다.
 * 입력값을 DTO로 묶어 Controller에 전달하고 결과를 출력하는 역할만 담당한다.
 */
public class ProductMenu {

    private final Scanner scanner;
    private final ProductController productController;
    private final CategoryController categoryController;

    public ProductMenu() {
        this.scanner = new Scanner(System.in);
        this.productController = new ProductController();
        this.categoryController = new CategoryController();
    }

    /** IntelliJ에서 이 클래스만 직접 실행할 때 사용하는 시작점이다. */
    public static void main(String[] args) {
        new ProductMenu().run();
    }

    /** 사용자가 0번을 선택할 때까지 상품 관리 메뉴를 반복해서 보여준다. */
    public void run() {
        while (true) {
            printMainMenu();
            int menuNumber = readInt("메뉴를 선택하세요 : ");

            try {
                switch (menuNumber) {
                    case 1 -> showAllProducts();
                    case 2 -> showProductByCode();
                    case 3 -> showProductsByCategory();
                    case 4 -> searchProductsByName();
                    case 5 -> registerProduct();
                    case 6 -> updateProduct();
                    case 7 -> openPromotionManagement();
                    case 0 -> {
                        System.out.println("상품 관리를 종료합니다.");
                        return;
                    }
                    default -> System.out.println("목록에 있는 메뉴 번호를 입력하세요.");
                }
            } catch (IllegalArgumentException exception) {
                /* Service에서 발견한 잘못된 입력값의 메시지를 사용자에게 보여준다. */
                System.out.println("입력 오류: " + exception.getMessage());
            } catch (SQLException exception) {
                /* JDBC 오류의 세부 내용 대신 사용자가 이해할 메시지만 출력한다. */
                System.out.println("DB 처리 중 오류가 발생했습니다: " + exception.getMessage());
            }

            System.out.println();
        }
    }

    private void printMainMenu() {
        System.out.println("========================================");
        System.out.println("          관리자 상품 관리      ");
        System.out.println("========================================");
        System.out.println("1. 판매 상품 전체 조회");
        System.out.println("2. 상품코드로 상세 조회");
        System.out.println("3. 카테고리별 상품 조회");
        System.out.println("4. 상품명 검색");
        System.out.println("5. 판매 상품 등록");
        System.out.println("6. 판매 상품 수정");

        System.out.println("0. 종료");
        System.out.println("========================================");
    }

    private void showAllProducts() throws SQLException {
        List<ProductDTO> products = productController.findAllProducts();
        printProducts(products);
    }

    private void showProductByCode() throws SQLException {
        int productCode = readInt("조회할 상품코드: ");
        ProductDTO product = productController.findProductByCode(productCode);

        if (product == null) {
            System.out.println("해당 상품을 찾을 수 없습니다.");
            return;
        }

        printProduct(product);
    }

    private void showProductsByCategory() throws SQLException {
        printCategoryGuide();
        int categoryCode = readInt("조회할 카테고리코드: ");
        List<ProductDTO> products = productController.findProductsByCategory(categoryCode);
        printProducts(products);
    }

    private void searchProductsByName() throws SQLException {
        String keyword = readText("검색할 상품명: ");
        List<ProductDTO> products = productController.searchProductsByName(keyword);
        printProducts(products);
    }

    private void registerProduct() throws SQLException {
        System.out.println("[판매 상품 등록]");

        String productName = readText("상품명: ");
        int productPrice = readInt("상품가격: ");
        int stockQuantity = readInt("재고수량: ");
        printCategoryGuide();
        int categoryCode = readInt("카테고리코드: ");

        /* 신규 등록이므로 AUTO_INCREMENT인 productCode는 생성자에 넣지 않는다. */
        ProductDTO product = new ProductDTO(
                productName,
                productPrice,
                stockQuantity,
                categoryCode
        );

        if (productController.registerProduct(product)) {
            System.out.println("상품 등록 성공. 생성된 상품코드: " + product.getProductCode());
        } else {
            System.out.println("상품을 등록하지 못했습니다.");
        }
    }

    private void updateProduct() throws SQLException {
        System.out.println("[판매 상품 수정]");

        int productCode = readInt("수정할 상품코드: ");
        ProductDTO existingProduct = productController.findProductByCode(productCode);

        if (existingProduct == null) {
            System.out.println("수정할 상품을 찾을 수 없습니다.");
            return;
        }

        System.out.println("현재 상품: ");
        printProduct(existingProduct);
        System.out.println("새로운 상품 정보를 입력하세요.");

        String productName = readText("상품명: ");
        int productPrice = readInt("상품가격: ");
        int stockQuantity = readInt("재고수량: ");
        printCategoryGuide();
        int categoryCode = readInt("카테고리코드: ");

        ProductDTO updatedProduct = new ProductDTO(
                productCode,
                productName,
                productPrice,
                stockQuantity,
                categoryCode
        );

        if (productController.updateProduct(updatedProduct)) {
            System.out.println("상품 수정 성공");
        } else {
            System.out.println("상품을 수정하지 못했습니다.");
        }
    }

    /**
     * 행사 담당 팀원의 메뉴로 이동할 연결 지점이다.
     * 행사 기능이 합쳐지기 전에는 안내만 출력하고, 합쳐진 뒤에는 이 메서드 내부에서
     * 팀원이 만든 PromotionMenu의 실행 메서드를 호출하면 된다.
     */
    private void openPromotionManagement() {
        System.out.println("[행사 관리]");
        System.out.println("행사 관리 기능은 담당 팀원의 기능과 연결될 예정입니다.");

        /*
         * TODO 행사 팀 코드가 합쳐지면 다음 두 작업만 수행한다.
         * 1. 파일 위쪽에 팀원의 PromotionMenu 클래스를 import한다.
         * 2. 위 안내 출력 대신 다음과 같이 행사 메뉴를 실행한다.
         *
         * new PromotionMenu().run();
         */
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

        System.out.println("조회된 상품 수: " + products.size());
        for (ProductDTO product : products) {
            printProduct(product);
        }
    }

    private void printProduct(ProductDTO product) {
        System.out.printf(
                "상품코드=%d | 상품명=%s | 가격=%d원 | 상태=%s | 재고=%d개 | 카테고리=%d%n",
                product.getProductCode(),
                product.getProductName(),
                product.getProductPrice(),
                product.getProductStatus(),
                product.getStockQuantity(),
                product.getCategoryCode()
        );
    }

    /** DB에 저장된 카테고리를 조회해 사용자가 선택하기 쉽게 보여준다. */
    private void printCategoryGuide() throws SQLException {
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
}
