package com.ohgiraffers.store.member.view;

import com.ohgiraffers.store.member.controller.MemberController;
import com.ohgiraffers.store.member.model.MemberDTO;
import com.ohgiraffers.store.payment.view.PaymentView;

import java.util.Scanner;

public class MemberView {

    // 화면 입력을 Controller에 전달하고 처리 결과를 출력하는 객체
    private final MemberController memberController = new MemberController();
    private final PaymentView paymentView = new PaymentView();
    // false가 되면 로그인 회원용 메뉴 반복을 종료
    boolean isLoggedIn = true;

    private Scanner sc = new Scanner(System.in);

    private int inputNumber(String prompt) {

        // 숫자가 아닌 값이 입력되면 메뉴를 종료하지 않고 다시 입력받음
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
        System.out.println("========================================");
        System.out.println("               회원가입");
        System.out.println("========================================");

        System.out.print("아이디 : ");
        String loginId = sc.nextLine();

        boolean available = memberController.isLoginIdAvailable(loginId);

        if(!available){
            System.out.println("메인 화면으로 이동합니다.");

            return null;
        }
        System.out.println("사용 가능한 아이디입니다.");
        System.out.println("----------------------------------------");

        System.out.print("비밀번호 : ");
        String password = sc.nextLine();

        System.out.print("닉네임 : ");
        String nickname = sc.nextLine();

        System.out.print("휴대폰 번호(010 제외 8자리) : ");
        String phone = sc.nextLine();

        if(!phone.matches("[0-9]{8}")){
            System.out.println("휴대폰 번호는 숫자 8자리로 입력해주세요.");
            System.out.println("메인 화면으로 이동합니다.");
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
    public boolean MyMembership(MemberDTO loggedInMember) {

        if (loggedInMember == null || loggedInMember.getMemberCode() <= 0){
            System.out.println("로그인 정보를 불러올 수 없습니다.");
            return false;
        }

        while(true){
            System.out.println();
            System.out.println("========================================");
            System.out.println("             MY MEMBERSHIP");
            System.out.println("========================================");
            System.out.println("  1. 내 정보 조회");
            System.out.println("  2. 결제 내역 조회");
            System.out.println("  3. 멤버십 정보 수정");
            System.out.println("  4. 멤버십 탈퇴");
            System.out.println("  5. 메인 화면으로");
            System.out.println("----------------------------------------");

            int menu = inputNumber("메뉴 번호를 입력해주세요 : ");

            switch (menu) {
                case 1:
                    selectMemberMenu(loggedInMember);
                    break;

                case 2:
                    paymentView.run(loggedInMember.getMemberCode());
                    break;

                case 3:
                    modifyMemberMenu(loggedInMember);
                    break;

                case 4:
                    // 탈퇴 성공 여부를 받아 성공한 경우 로그인 회원용 화면을 종료
                    boolean withdrawn = withdrawMemberMenu(loggedInMember);

                    if(withdrawn){
                       return true;
                    }
                    break;

                case 5:
                    System.out.println("메인 화면으로 이동합니다.");
                    return false;

                default:
                    System.out.println("1~5 사이의 숫자를 입력해주세요.");
            }
        }
    }

    // MyMembership - 내정보 조회
    public void selectMemberMenu(MemberDTO loggedInMember) {

        while (isLoggedIn) {

        // 로그인 시 저장한 회원 코드를 기준으로 DB의 최신 회원 정보를 다시 조회
        MemberDTO memberinfo = memberController.selectMember(loggedInMember);
        if (memberinfo == null){
            System.out.println("멤버십 정보를 조회할 수 없습니다.");
            return;
        }

        String gradeName = memberController.selectGradeName(memberinfo);

            System.out.println();
            System.out.println("========================================");
            System.out.println("              멤버십 정보");
            System.out.println("========================================");
            System.out.println(" 멤버십 등급 : " +  gradeName);
            System.out.println(" 아이디 : " +  memberinfo.getLoginId());
            System.out.println(" 비밀번호 : ********");
            System.out.println(" 닉네임 : " + memberinfo.getNickname());
            System.out.printf(" 휴대폰 번호 : %08d%n", memberinfo.getPhone());
            System.out.println(" 보유 포인트 : " + memberinfo.getPointBalance());
            System.out.println(" 총 구매 누적 금액 : " + memberinfo.getTotalAmount());

            System.out.println("----------------------------------------");
            System.out.println("  1. 멤버십 정보 수정");
            System.out.println("  2. 이전 화면으로");
            System.out.println("  3. 프로그램 종료");
            System.out.println("----------------------------------------");


            int menu = inputNumber("메뉴 번호를 입력해주세요 : ");

            switch (menu) {
                case 1:
                    modifyMemberMenu(loggedInMember);
                    break;

                case 2:
                    System.out.println("이전 화면으로 이동합니다.");
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
        System.out.println("========================================");
        System.out.println("             회원정보 수정");
        System.out.println("========================================");
        System.out.println("[안내] 아이디는 수정할 수 없습니다.");
        System.out.println("----------------------------------------");

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

        System.out.println("----------------------------------------");

        if(!modified){
            System.out.println("[실패] 멤버십 정보 수정에 실패했습니다.");
            return;
        }

        System.out.println("[성공] 멤버십 정보가 수정되었습니다.");

        System.out.println("========================================");
    }

    public boolean withdrawMemberMenu(MemberDTO loggedInMember) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("               회원 탈퇴");
        System.out.println("========================================");
        System.out.println("[주의] 탈퇴한 계정은 복구할 수 없습니다.");
        System.out.println("[주의] 보유 포인트와 누적금액이 소멸됩니다.");
        System.out.println("----------------------------------------");
        System.out.println("  1. 탈퇴 진행");
        System.out.println("  2. 탈퇴 취소");
        System.out.println("----------------------------------------");

        int number = inputNumber("메뉴 번호를 입력해주세요 : ");

        if (number == 2) {
            System.out.println("이전 메뉴로 돌아갑니다.");
            return false;
        }

        if (number != 1) {
            System.out.println("1 또는 2를 입력해주세요.");
            return false;
        }
        // true는 탈퇴 성공, false는 취소 또는 탈퇴 실패를 의미
        boolean withdrawn = memberController.withdrawMember(loggedInMember);

        if (!withdrawn) {
            System.out.println("멤버십 탈퇴 처리에 실패했습니다.");
            return false;
        }

        System.out.println("멤버십 탈퇴가 완료되었습니다.");

        return true;
    }
    }
