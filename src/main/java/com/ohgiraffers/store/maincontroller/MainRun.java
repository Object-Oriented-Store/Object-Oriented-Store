package com.ohgiraffers.store.maincontroller;

import com.ohgiraffers.store.member.model.MemberDTO;

import java.util.Scanner;

public class MainRun {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Controller controller = new Controller(sc);

    while (true) {
        // 로그인 또는 회원가입 결과를 한 번만 받는다.
        MemberDTO loggedInMember = controller.Start();

        // 프로그램 종료를 선택했다면 실행을 끝낸다.
        if (loggedInMember == null) {
            return;
        }

        // 관리자 계정과 일반회원 계정의 시작 화면을 구분한다.
        if ("admin".equals(loggedInMember.getLoginId())) {
            controller.startManager();

            return;
        }
        boolean withdrawn = controller.startMember(loggedInMember);

        if (!withdrawn) {
            return;
        }

        System.out.println();
        System.out.println("탈퇴가 완료되었습니다.");
    }
    }
}
