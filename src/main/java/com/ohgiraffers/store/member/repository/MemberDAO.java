package com.ohgiraffers.store.member.repository;

import com.ohgiraffers.store.member.model.MemberDTO;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static com.ohgiraffers.store.common.config.DBConnection.getConnection;

public class MemberDAO {

    Connection con;


    PreparedStatement pstmt = null;
    ResultSet rset = null;

    MemberDTO member = null;
    Properties prop = new Properties();

    {
        try {
    prop.loadFromXML(
         new FileInputStream("src/main/java/com/ohgiraffers/store/member/repository/member-query.xml")
            );

    {
        try {
            con = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };








        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




}
