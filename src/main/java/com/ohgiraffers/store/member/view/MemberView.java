package com.ohgiraffers.store.member.view;

import com.ohgiraffers.store.member.controller.MemberController;

import java.util.Scanner;

public class MemberView {

    private MemberController memberController = new MemberController();

    private Scanner sc = new Scanner(System.in);

    public void displayMainMenu(){

        boolean isRunning = true;

        while (isRunning){

            System.out.println();
            System.out.println("-------**Object_oriented_Store**-------");
            System.out.println("--------Online 24시 편의점 객체지향점입니다--------");
            System.out.println("1. 로그인 ");
            System.out.println("2. 회원가입 ");
            System.out.println("3. 프로그램 종료 ");
            System.out.println("========================================");

            int num = inputNumber("번호를 입력해주세요 : ");

            switch (num){
                case 1 :
                    // 로그인 기능
                    break;
                case 2 :
                    // 회원 가입 기능
                    break;
                case 3 :
                    System.out.println("프로그램을 종료합니다.");
                    isRunning = false;
                    break;
                default:
                    System.out.println("1~3 사이의 숫자 중 하나만 입력해주세요.");
            }
        }
    }

    private int inputNumber(String prompt) {

        while(true){

            System.out.print(prompt);
            String input = sc.nextLine();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력해주세요.");
            }
        }
    }
}
