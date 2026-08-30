package com.ohgiraffers.store.promotion.controller;

import com.ohgiraffers.store.promotion.service.PromotionService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class PromotionRun {
    Scanner sc;
    public PromotionRun(Scanner sc) {
        this.sc = sc;
    }

    Connection conn;

    {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/object_oriented_store", "oodbms", "oodbms");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    public void promotionMembership(){
        PromotionService service = new PromotionService();
        System.out.println("======이곳은 행사 페이지 입니다.======");
        System.out.println("1. 진행중인 행사 조회");
        System.out.println("2. 이전 메뉴로 이동");
        int selectNum =  inputNumber("메뉴를 선택하세요: ");
        boolean ctrl = true;

        try {
            while(ctrl) {
                switch (selectNum) {
                    case 1:
                        service.printCurrentlyPromotion(conn);
                        ctrl = false;


                    case 2:
                        //이전으로 돌아가는 메소드 호출
                        ctrl = false;
                        break;

                    default:
                        System.out.println("잘못된 번호 입력입니다.");
                        selectNum = inputNumber("번호를 다시 입력하세요: ");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    public void promotionManager(){
        PromotionService service = new PromotionService();
        System.out.println("======이곳은 관리자용 행사 페이지 입니다.======");
        System.out.println("1. 진행중인 행사 조회");
        System.out.println("2. 새로운 행사 등록");
        System.out.println("3. 기존 행사 수정");
        System.out.println("4. 기존 행사 삭제");
        int selectNum =  inputNumber("메뉴를 선택하세요: ");
        boolean ctrl = true;

        try {
            while(ctrl) {
                switch (selectNum) {
                    case 1:
                        Connection conn=null;
                        service.printCurrentlyPromotion(conn);
                        ctrl = false;
                        break;
                    case 2:
                        //이전으로 돌아가는 메소드 호출
                        ctrl = false;
                        break;
                    case 3:

                    default:
                        System.out.println("잘못된 번호 입력입니다.");
                        selectNum = inputNumber("번호를 다시 입력하세요: ");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
