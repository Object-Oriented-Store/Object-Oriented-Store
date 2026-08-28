package com.ohgiraffers.store.member.view;

import com.ohgiraffers.store.member.controller.MemberController;
import com.ohgiraffers.store.member.model.MemberDTO;

import java.util.Scanner;

public class MemberView {

    private final MemberController memberController = new MemberController();
    private final MemberDTO memberDTO = new MemberDTO();
    private MemberDTO loggedInMember;

    boolean isLoggedIn = true;

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
                    loginMember();
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

    public void joinMember(){

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

        while(true){
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

            modifyMemberMenu();
            return;
    }
}
    public void MyMembershipMenu() {
        while(loggedInMember != null){
            System.out.println();
            System.out.println("========== My 멤버십 ==========");
            System.out.println("  1. 내 정보 조회");
            System.out.println("  2. 주문 내역 확인");
            System.out.println("  3. 회원정보 수정");
            System.out.println("  4. 메인 화면으로");
            System.out.println("  5. 프로그램 종료");
            System.out.println("--------------------------------");

            int menu = inputNumber("메뉴 번호를 입력해주세요 : ");

            switch (menu) {
                case 1:
                    selectMemberMenu();
                    break;

                case 2:
                    System.out.println("주문 내역 조회 기능으로 이동합니다.");
                    // 주문 내역 조회 메서드 호출
                    break;

                case 3:
                    modifyMemberMenu();
                    break;

                case 4:
                    loggedInMember = null;
                    System.out.println("메인 화면으로 이동합니다.");
                    return;

                case 5:
                    loggedInMember = null;
                    isLoggedIn = false;
                    System.out.println("프로그램을 종료합니다.");
                    return;

                default:
                    System.out.println("1~5 사이의 숫자를 입력해주세요.");
            }
        }
    }

    public void selectMemberMenu() {

        while (isLoggedIn && loggedInMember != null) {
            System.out.println();
            System.out.println("========== My 멤버십 상세 ==========");
            System.out.println("(수정 가능한 정보 : 비밀번호, 닉네임, 휴대폰 번호)");
            System.out.println(" 아이디 : " +  memberDTO.getLoginId());
            System.out.println(" 비밀번호 : ********" + memberDTO.getPassword());
            System.out.println(" 닉네임 : " + memberDTO.getNickname());
            System.out.println(" 휴대폰 번호 : " + memberDTO.getPhone());
            System.out.println(" 보유 포인트 : " + memberDTO.getPointBalance());
            System.out.println(" 총 구매 누적 금액 : " + memberDTO.getTotalAmount());
            System.out.println("----------------------------------------");

            System.out.println();
            System.out.println("  1. 회원정보 수정");
            System.out.println("  2. 메인 화면으로");
            System.out.println("  3. 프로그램 종료");
            System.out.println("--------------------------------");



            int menu = inputNumber("메뉴 번호를 입력해주세요 : ");

            switch (menu) {
                case 1:
                    modifyMemberMenu();
                    break;
                case 2:
                    loggedInMember = null;
                    System.out.println("메인 화면으로 이동합니다.");
                    return;

                case 3:
                    loggedInMember = null;
                    isLoggedIn = false;
                    System.out.println("프로그램을 종료합니다.");
                    return;

                default:
                    System.out.println("1~3 사이의 숫자를 입력해주세요.");
            }
        }
    }

    public void modifyMemberMenu() {

        System.out.println();
        System.out.println("----------- 회원정보 수정 -----------");
        System.out.println("아이디 : " + loggedInMember.getLoginId());
        System.out.println("※ 아이디는 수정할 수 없습니다.");

        System.out.println("새 비밀번호 : ");
        String password = sc.nextLine();

        if(password.isEmpty()){
            System.out.println("비밀번호를 입력해주세요.");
            return;
        }

        System.out.println("새 닉네임 : ");
        String nickname = sc.nextLine();

        if(nickname.isBlank()){
            System.out.println("닉네임을 입력해주세요.");
            return;
        }

        System.out.println("새 휴대폰 번호(010 제외 8자리) : ");
        String phoneInput = sc.nextLine();

        if(!phoneInput.matches("[0-9]{8}")){
            System.out.println("휴대폰 번호는 숫자 8자리로 입력해주세요.");
            return;
        }

        int phone = Integer.parseInt(phoneInput);

        MemberDTO modifiedMember = new MemberDTO(loggedInMember.getMemberCode(),
                password, nickname, phone);


        boolean modified = memberController.modifyMember(modifiedMember);

        if(!modified){
            System.out.println("멤버십 정보 수정에 실패했습니다.");
            return;
        }

        loggedInMember = memberController.selectMember(loggedInMember);

        System.out.println("멤버십 정보가 수정되었습니다.");

        }
    }
