package com.ohgiraffers.store.maincontroller;

import com.ohgiraffers.store.member.model.MemberDTO;
import com.ohgiraffers.store.member.model.Membership;
import com.ohgiraffers.store.member.service.MemberService;
import com.ohgiraffers.store.member.view.MemberView;
import com.ohgiraffers.store.product.view.ProductMenu;
import com.ohgiraffers.store.promotion.model.PromotionDAO;
import com.ohgiraffers.store.promotion.service.PromotionService;
import com.ohgiraffers.store.promotion.service.SettingsOnlyManager;
import com.ohgiraffers.store.order.view.OrderPurchaseView;
import com.ohgiraffers.store.order.view.OrderView;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Controller {
    Scanner sc = new Scanner(System.in);
    Membership mb;
    Properties prop = new Properties();
    MemberService memberService = new MemberService();
    MemberView memberView = new MemberView();
    MemberDTO membe = new MemberDTO();

    public Controller(Scanner sc) {
        this.sc = sc;
        this.mb = new Membership(sc);

        try {
            prop.loadFromXML(new FileInputStream(
                    "src/main/java/com/ohgiraffers/store/common/mapper/main-query.xml"
            ));
        } catch (IOException e) {
            throw new RuntimeException("쿼리 XML을 읽을 수 없습니다.", e);
        }
    }

    public Controller() {

    }

    // 숫자가 아닌 입력으로 프로그램이 종료되지 않도록 정수 입력을 반복 처리
    private int inputNumber(String prompt) {

        while (true) {
            System.out.print(prompt);

            // 한 줄 전체 입력 후 앞뒤 공백 제거
            String input = sc.nextLine().trim();

            try {
                // 문자열을 int로 변환
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력해주세요.");
            }
        }
    }


    // 프로그램 실행 첫 화면
    public MemberDTO Start() {

        boolean sw = true;
        MemberDTO loggedInMember;
        while (sw) {
            System.out.println("==============객체지향점==============");
            System.out.println("어서오세요, 객체지향 편의점 입니다~!");
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.println("3. 프로그램 종료");

            int choice1 = inputNumber("메뉴를 정수로 입력하세요 : ");

            switch (choice1) {
                case 1:
                    loggedInMember = mb.logIn();
                    if (loggedInMember != null) {
                        return loggedInMember;
                    }
                    break;
                case 2:
                    loggedInMember = memberView.joinMember();

                    if (loggedInMember != null) {
                        return loggedInMember;
                    }
                    break;
                case 3:
                    System.out.println("프로그램을 종료합니다. " +
                            "");
                    sw = false;
                    break;
                default:
                    System.out.println("잘못입력하셨습니다. 메인화면으로 돌아갑니다.");

            }
        }

        return null;
    }

    // 멤버십 회원 메인 화면
    public boolean startMember(MemberDTO loggedInMember) {

        if (loggedInMember == null
                || loggedInMember.getMemberCode() <= 0) {
            System.out.println("로그인 정보가 없습니다.");
            return false;
        }

        String nickname = loggedInMember.getNickname();

        while (true) {
            System.out.printf("%s 고객님, \n객체지향점에 오신 걸 환영합니다!\n", nickname);
            System.out.println("==============객체지향점==============");
            System.out.println("[멤버십 전용 메뉴]");
            System.out.println("1. 구매하기");
            System.out.println("2. My Membership");
            System.out.println("3. 프로그램 종료");
            System.out.println("======================================");

            int choice2 = inputNumber("메뉴를 정수로 입력하세요 : ");

            switch (choice2) {
                case 1: {
                    int purchaseResult =
                            new OrderPurchaseView(sc).run(
                                    loggedInMember.getMemberCode()
                            );
                    if (purchaseResult
                            == OrderView.REQUEST_PAYMENT) {

                        System.out.println(
                                "결제 화면 연결이 필요합니다."
                        );
                    }
                    break;
                }
                case 2:
                    boolean withdrawn = memberView.MyMembership(loggedInMember);

                    if (withdrawn) {
                        return true;
                    }

                    break;
                case 3:
                    System.out.println("프로그램을 종료합니다.");
                    return false;
                default:
                    System.out.println("잘못입력하셨습니다. 다시 입력하세요.");
                    break;
            }
        }
    }

    public void SelectCategory(String userName) {
        System.out.printf("%s 고객님, \n객체지향점에 오신 걸 환영합니다!\n", userName);
        System.out.println("==============카테고리==============");
        System.out.println("1. 행사제품");
        System.out.println("2. 라면류");
        System.out.println("3. 과자류");
        System.out.println("4. 간편식품");
        System.out.println("5. 신선제품");
        System.out.println("6. 음료");
        System.out.println("7. 아이스크림");
        System.out.println("8. 생활용품");
        System.out.println("9. 주류");
        System.out.println("10. 담배");
        System.out.println("====================================");

        int choice3 = inputNumber("메뉴를 정수로 입력하세요 : ");

        String sql = prop.getProperty("selectCategory");
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/object_oriented_store", "oodbms", "oodbms");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, choice3);

            System.out.println("===================================");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("상품명: " + rs.getString("product_name"));
                System.out.println("가격: " + rs.getString("product_price") + "원");
                System.out.println();
            }
            sc.nextLine();
            System.out.println("===================================");
            System.out.println("구매할 상품명을 입력하세요: ");
            String selectProductName = sc.nextLine();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 관리자 메인 화면
    public void startManager() {
        PromotionDAO pdao = new PromotionDAO();
        PromotionService service = new PromotionService();
        SettingsOnlyManager som = new SettingsOnlyManager(sc);
        ProductMenu pm = new ProductMenu();
        System.out.println("===============객체지향점===============");
        System.out.println("[관리자용 메뉴]");
        System.out.println("1. 기존 행사 등록");
        System.out.println("2. 기존 행사 수정");
        System.out.println("3. 기존 행사 삭제");
        System.out.println("4. 기존 행사에 행사상품을 추가");
        System.out.println("5. 판매 상품 관리");
        System.out.println("6. 메인 화면으로 이동");
        System.out.println("======================================");

        int choice4 = inputNumber("메뉴를 정수로 입력하세요 : ");

        switch (choice4) {
            case 1:
                som.RegisterPromotion();
                startManager();
                break;

            case 2:
                try {
                    som.UpdatePromotion();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                startManager();
                break;

            case 3:
                som.DeletePromotion();
                startManager();
                break;

            case 4:
                try {
                    som.RegisterPromotionProduct();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                startManager();
                break;

            case 5:
                pm.run();
                startManager();
                break;

            case 6:
                Start();
                break;

            default:
                System.out.println("잘못입력하셨습니다. 다시 입력하세요.");
                startManager();
                break;
        }
    }
}