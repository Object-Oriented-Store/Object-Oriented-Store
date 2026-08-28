package com.ohgiraffers.store.member.model;

import java.sql.*;
import java.util.Scanner;

public class Membership<SqlSession> {
    Scanner sc;
    public Membership(Scanner sc) {
        this.sc=sc;
    }

    public Membership() {}


    public String logIn() {
        System.out.println("==============로그인==============");
        sc.nextLine();
        System.out.print("아이디 입력: ");
        String loginId = sc.nextLine();
        System.out.print("비밀번호 입력: ");
        String password = sc.nextLine();
        String sql = "SELECT nickname FROM tbl_member WHERE login_id=? AND password=?";
        String returnname = "";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/object_oriented_store", "oodbms", "oodbms");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loginId);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("[로그인 성공]");
                System.out.println();
                returnname = rs.getString("nickname");

            } else {
                System.out.println("아이디 또는 비밀번호가 틀렸습니다.");
                logIn();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return returnname;
    }
        public void createMembership(){
        System.out.println("==============멤버십가입==============");
        System.out.print("사용할 아이디 입력: ");
        String loginId = sc.nextLine();
        System.out.print("사용할 비밀번호 입력: ");
        String password = sc.nextLine();
        System.out.println("사용할 닉네임 입력: ");
        String nickname = sc.nextLine();
        System.out.println("휴대폰 번호 입력( - 제외): ");

        int phone = sc.nextInt();
        String sql = "INSERT INTO tbl_member VALUES (login_id, password, nickname, phone)";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/object_oriented_store", "oodbms", "oodbms");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("==============멤버십가입==============");
        System.out.println("아이디: " + loginId);
        System.out.println("비밀번호: " + password);
        System.out.println("닉네임: " +  nickname);
        System.out.println("휴대폰 번호: " + phone);
        System.out.print("멤버십 가입을 환영합니다. \n로그인 화면으로 이동합니다.");
    }
}
