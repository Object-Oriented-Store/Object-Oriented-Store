package com.ohgiraffers.store.member.model;

import com.ohgiraffers.store.common.config.DBConnection;

import java.sql.*;
import java.util.Scanner;

public class Membership<SqlSession> {
    Scanner sc;

    public Membership(Scanner sc) {
        this.sc=sc;
    }

    public Membership() {}

    public MemberDTO logIn() {

        while (true) {
            System.out.println("==============로그인==============");

            System.out.print("아이디 입력: ");
            String loginId = sc.nextLine();

            System.out.print("비밀번호 입력: ");
            String password = sc.nextLine();
            String sql = "SELECT member_code, login_id, nickname FROM tbl_member WHERE login_id=? AND password=?";
            MemberDTO loggedInMember = null;

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, loginId);
                pstmt.setString(2, password);

                try (ResultSet rs = pstmt.executeQuery()) {

                    if (rs.next()) {

                        loggedInMember = new MemberDTO(
                                rs.getInt("member_code"),
                                rs.getString("login_id"),
                                rs.getString("nickname")
                        );

                        System.out.println("[로그인 성공]");
                        System.out.println();

                    } else {
                        System.out.println("아이디 또는 비밀번호가 틀렸습니다.");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return loggedInMember;
        }
    }
}
