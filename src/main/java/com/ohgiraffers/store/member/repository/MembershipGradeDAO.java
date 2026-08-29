package com.ohgiraffers.store.member.repository;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static com.ohgiraffers.store.common.config.DBConnection.getConnection;

public class MembershipGradeDAO {

    // 멤버십 등급명, 등급 갱신, 적립률 조회 SQL을 보관
    private final Properties prop = new Properties();

    public MembershipGradeDAO() {
        try (FileInputStream fis = new FileInputStream("src/main/java/com/ohgiraffers/store/member/repository/member-query.xml")) {
            prop.loadFromXML(fis);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("XML 파일을 찾을 수 없습니다.", e);

        } catch (IOException e) {
            throw new RuntimeException("멤버십 등급 쿼리 파일을 읽지 못했습니다", e);
        }
    }

        // 등급 코드에 해당하는 고객 표시용 등급명을 조회
        public String selectGradeName(int gradeCode){
            String query = prop.getProperty("selectGradeName");

            try (Connection con = getConnection();
                 PreparedStatement pstmt = con.prepareStatement(query)) {

                pstmt.setInt(1, gradeCode);


                try (ResultSet rset = pstmt.executeQuery()) {
                    if (rset.next()) {
                        return rset.getString("grade_name");
                    }
                }
                return null;

            } catch (SQLException e) {
                throw new RuntimeException("멤버십 등급명 조회 중 오류가 발생했습니다.", e);
            }
        }

        // 회원의 현재 누적 구매 금액을 기준으로 등급 코드를 갱신
        public int updateMembershipGrade(int memberCode){
            String query = prop.getProperty("updateMembershipGrade");

            try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(query)){

                pstmt.setInt(1, memberCode);

                return pstmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        // 회원의 현재 등급에 설정된 포인트 적립률을 조회
        public int selectRewardRate(int memberCode){
            String query = prop.getProperty("selectRewardRate");

            try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(query)){

                pstmt.setInt(1,memberCode);

                try(ResultSet rset = pstmt.executeQuery()){

                    if (rset.next()){
                        return rset.getInt("reward_rate");
                    }
                }
                return 0;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
