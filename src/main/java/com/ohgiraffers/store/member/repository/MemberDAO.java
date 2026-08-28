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
}
