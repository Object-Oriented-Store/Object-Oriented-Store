package com.ohgiraffers.store.member;

import com.ohgiraffers.store.member.view.MemberView;

public class Application {

    public static void main(String[] args) {
        MemberView memberView = new MemberView();

        memberView.displayMainMenu();
    }
}
