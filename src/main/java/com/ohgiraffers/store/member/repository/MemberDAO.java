package com.ohgiraffers.store.member.repository;

import com.ohgiraffers.store.member.model.MemberDTO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static com.ohgiraffers.store.common.config.DBConnection.getConnection;

public class MemberDAO {

    private final Properties prop = new Properties();

    public MemberDAO(){
        try (FileInputStream fis = new FileInputStream("src/main/java/com/ohgiraffers/store/member/repository/member-query.xml"
        )) { prop.loadFromXML(fis);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("XML 파일을 읽어오지 못했습니다.", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // 아이디 중복 확인
    public boolean isLoginIdDuplicate(String loginId) {

        String query = prop.getProperty("checkDuplicateId");

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, loginId);

            try (ResultSet rset = pstmt.executeQuery()){


                // 동일 아이디가 1개 이상일 시 중복으로 판단
                if (rset.next()) {
                    int memberCount = rset.getInt("member_count");
                    return memberCount > 0;
                }
                return false;
            }
            } catch(SQLException e){
                throw new RuntimeException("아이디 중복 확인 중 오류가 발생되었습니다.", e);
            }
        }

    // 회원가입 정보 받기
    public int insertMember(MemberDTO member){

        String query = prop.getProperty("insertMemberJoin");

        try (Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, member.getLoginId());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getNickname());
            pstmt.setInt(4, member.getPhone());

            int result = pstmt.executeUpdate();

            return result;

        } catch (SQLException e) {
            throw new RuntimeException("회원가입 중 오류가 발생되었습니다.", e);
        }
    }

    // 회원 가입 후 즉시 로그인을 위한 SQL
    public MemberDTO selectMemberByLoginId(String loginId) {

        String query = prop.getProperty("selectMemberByLoginId");

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, loginId);

            try (ResultSet rset = pstmt.executeQuery()) {

                if (rset.next()) {
                    return new MemberDTO(
                            rset.getInt("member_code"),
                            rset.getInt("grade_code"),
                            rset.getString("login_id"),
                            rset.getString("password"),
                            rset.getString("nickname"),
                            rset.getInt("phone"),
                            rset.getInt("point_balance"),
                            rset.getInt("total_amount")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "가입 회원 조회 중 오류가 발생했습니다.", e
            );
        }
        return null;
    }


    // 정보 조회
    public MemberDTO selectMember(MemberDTO member) {
        String query = prop.getProperty("selectMemberInfo");
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, member.getMemberCode());

            ResultSet rset = pstmt.executeQuery();

            if (rset.next()) {
                return new MemberDTO(
                        rset.getInt("member_code"),
                        rset.getInt("grade_code"),
                        rset.getString("login_id"),
                        rset.getString("password"),
                        rset.getString("nickname"),
                        rset.getInt("phone"),
                        rset.getInt("point_balance"),
                        rset.getInt("total_amount")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("정보 조회 중 오류가 발생되었습니다.", e);
        }
        return null;
    }

    // 정보 변경
    public int modifyMember(MemberDTO member){
        String query = prop.getProperty("modifyMemberInfo");

        try (Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement(query)){

            pstmt.setString(1, member.getPassword());
            pstmt.setString(2, member.getNickname());
            pstmt.setInt(3, member.getPhone());
            pstmt.setInt(4, member.getMemberCode());

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("멤버십 정보 변경 중 오류가 발생되었습니다.", e);
        }
    }

    public int plusTotalAmount(int memberCode, int finalAmount) {
        String query = prop.getProperty("increaseTotalAmount");

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, finalAmount);
            pstmt.setInt(2, memberCode);

        return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("누적 금액 증가 오류가 발생되었습니다.", e);
        }
    }

    public int minusTotalAmount(int memberCode, int previousAmount) {
        String query = prop.getProperty("decreaseTotalAmount");

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, previousAmount);
            pstmt.setInt(2, memberCode);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("누적 금액 차감 오류가 발생되었습니다.", e);
        }
    }
    }

