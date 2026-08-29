package com.ohgiraffers.store.member.view;

import com.ohgiraffers.store.member.controller.MemberController;
import com.ohgiraffers.store.member.model.MemberDTO;
import com.ohgiraffers.store.member.model.MembershipGradeDTO;

import java.util.Scanner;

public class MemberView {

    private final MemberController memberController = new MemberController();
    boolean isLoggedIn = true;

    private Scanner sc = new Scanner(System.in);

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

    // 회원 가입 화면
    public MemberDTO joinMember(){

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
            return null;
        }

        int phonenum =  Integer.parseInt(phone);

        MemberDTO joinedMember = memberController.joinMember(loginId, password, nickname, phonenum);

        if(joinedMember == null) {
            System.out.println("멤버십 가입이 되지 않았습니다.");
            System.out.println("메인 화면으로 이동합니다.");
            return null;
        }
        System.out.println("멤버십 가입에 가입되었습니다.");
        return joinedMember;
    }

    //My Membership 선택 시 보여지는 화면
    public void MyMembership(MemberDTO loggedInMember) {

        if (loggedInMember == null || loggedInMember.getMemberCode() <= 0){
            System.out.println("로그인 정보를 불러올 수 없습니다.");
            return;
        }

        while(isLoggedIn){
            System.out.println("=============My Membership============");
            System.out.println("  1. 내 정보 조회");
            System.out.println("  2. 주문 내역 확인");
            System.out.println("  3. 회원정보 수정");
            System.out.println("  4. 메인 화면으로");
            System.out.println("  5. 프로그램 종료");
            System.out.println("======================================");

            int menu = inputNumber("메뉴 번호를 입력해주세요 : ");

            switch (menu) {
                case 1:
                    selectMemberMenu(loggedInMember);
                    break;

                case 2:
                    // 주문 내역 조회 메서드 호출
                    break;

                case 3:
                    modifyMemberMenu(loggedInMember);
                    break;

                case 4:
                    System.out.println("메인 화면으로 이동합니다.");
                    return;

                case 5:
                    isLoggedIn = false;
                    System.out.println("프로그램을 종료합니다.");
                    return;

                default:
                    System.out.println("1~5 사이의 숫자를 입력해주세요.");
            }
        }
    }

    // MyMembership - 내정보 조회
    public void selectMemberMenu(MemberDTO loggedInMember) {

        MemberDTO memberinfo = memberController.selectMember(loggedInMember);
        if (memberinfo == null){
            System.out.println("멤버십 정보를 조회할 수 없습니다.");
            return;
        }

        String gradeName = memberController.selectGradeName(memberinfo);

        while (isLoggedIn) {
            System.out.println();
            System.out.println("========== My 멤버십 상세 ==========");
            System.out.println(" 멤버십 등급 : " +  gradeName);
            System.out.println(" 아이디 : " +  memberinfo.getLoginId());
            System.out.println(" 비밀번호 : ********");
            System.out.println(" 닉네임 : " + memberinfo.getNickname());
            System.out.printf(" 휴대폰 번호 : %08d%n", memberinfo.getPhone());
            System.out.println(" 보유 포인트 : " + memberinfo.getPointBalance());
            System.out.println(" 총 구매 누적 금액 : " + memberinfo.getTotalAmount());
            System.out.println("----------------------------------------");

            System.out.println("  1. 회원정보 수정");
            System.out.println("  2. 메인 화면으로");
            System.out.println("  3. 프로그램 종료");
            System.out.println("--------------------------------");


            int menu = inputNumber("메뉴 번호를 입력해주세요 : ");

            switch (menu) {
                case 1:
                    modifyMemberMenu(loggedInMember);
                    break;
                case 2:
                    System.out.println("메인 화면으로 이동합니다.");
                    return;

                case 3:
                    isLoggedIn = false;
                    System.out.println("프로그램을 종료합니다.");
                    return;

                default:
                    System.out.println("1~3 사이의 숫자를 입력해주세요.");
            }
        }
    }

    // 멤버십 정보 수정
    public void modifyMemberMenu(MemberDTO loggedInMember) {

        System.out.println();
        System.out.println("----------- 회원정보 수정 -----------");
        System.out.println("※ 아이디는 수정할 수 없습니다.");

        System.out.print("새 비밀번호 : ");
        String password = sc.nextLine();

        System.out.print("새 닉네임 : ");
        String nickname = sc.nextLine();

        System.out.print("새 휴대폰 번호(010 제외 8자리) : ");
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

        System.out.println("멤버십 정보가 수정되었습니다.");

        }
    }
