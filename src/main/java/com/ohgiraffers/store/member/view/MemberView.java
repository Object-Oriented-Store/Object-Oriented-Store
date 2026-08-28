package com.ohgiraffers.store.member.view;

import com.ohgiraffers.store.member.controller.MemberController;
import com.ohgiraffers.store.member.model.MemberDTO;

import java.util.Scanner;

public class MemberView {

    private final MemberController memberController = new MemberController();
    private MemberDTO loggedInMember;

    private Scanner sc = new Scanner(System.in);

    public void displayMainMenu(){

        boolean isRunning = true;

        while (isRunning){

            System.out.println();
            System.out.println("==========================================");
            System.out.println("          OBJECT-ORIENTED STORE");
            System.out.println("        24시간 온라인 편의점입니다.");
            System.out.println("==========================================");
            System.out.println("  1. 로그인");
            System.out.println("  2. 회원가입");
            System.out.println("  3. 프로그램 종료");
            System.out.println("------------------------------------------");

            int num = inputNumber("메뉴 번호를 입력해주세요 : ");

            switch (num){
                case 1 :
                    // 로그인 기능
                    break;
                case 2 :
                    joinMember();
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

    private void joinMember(){

        while(true){
        System.out.println();
        System.out.println(" -------------- 회원 가입 -------------");

        System.out.print("아이디 : ");
        String loginId = sc.nextLine();

        System.out.print("비밀번호 : ");
        String password = sc.nextLine();

        System.out.print("닉네임 : ");
        String nickname = sc.nextLine();

        System.out.print("휴대폰 번호(010 제외 8자리) : ");
        String phone = sc.nextLine();

        if(!phone.matches("[0-9]{8}")){
            System.out.println("휴대폰 번호는 숫자 8자리로 입력해주세요.");
            return;
        }

        int phonenum =  Integer.parseInt(phone);

        boolean joined = memberController.joinMember(loginId, password, nickname, phonenum);

        if(joined) {
            System.out.println("멤버십에 가입되었습니다.");
            return;
        }
            System.out.println("멤버십 가입에 실패하였습니다.");

    }
}

    private void loginMember() {

        System.out.println();
        System.out.println("--------------- 로그인 ---------------");

        System.out.print("아이디: ");
        String loginId = sc.nextLine();

        System.out.print("비밀번호: ");
        String password = sc.nextLine();

        MemberDTO member =
                memberController.loginMember(loginId, password);

        if (member == null) {
            System.out.println("아이디 또는 비밀번호가 일치하지 않습니다.");
            return;
        }

        loggedInMember = member;

        System.out.println("로그인되었습니다.");
        System.out.println(member.getNickname() + "님 " );
    }
}