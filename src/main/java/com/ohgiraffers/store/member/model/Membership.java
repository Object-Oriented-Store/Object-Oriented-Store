package com.ohgiraffers.store.member.model;

import com.ohgiraffers.store.maincontroller.Controller;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Membership<SqlSession> {
    Scanner sc;
    Controller cl = new Controller();
    public Membership(Scanner sc) {
        this.sc=sc;
    }

    public Membership() {}


    public MemberDTO logIn() {
        System.out.println("==============로그인==============");
        sc.nextLine();
        System.out.print("아이디 입력: ");
        String loginId = sc.nextLine();
        System.out.print("비밀번호 입력: ");
        String password = sc.nextLine();
        String sql = "SELECT member_code, nickname FROM tbl_member WHERE login_id=? AND password=?";
        MemberDTO loggedInMember = null;
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/object_oriented_store", "oodbms", "oodbms");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loginId);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()){

            if (rs.next()) {

                loggedInMember = new MemberDTO(
                        rs.getInt("member_Code"),
                        rs.getString("nickname")
                );

                System.out.println("[로그인 성공]");
                System.out.println();
                if(Objects.equals(loggedInMember.getLoginId(), "admin")){
                    cl.startManager();
                }

            } else {
                System.out.println("아이디 또는 비밀번호가 틀렸습니다.");
                logIn();
            }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loggedInMember;
    }

}
