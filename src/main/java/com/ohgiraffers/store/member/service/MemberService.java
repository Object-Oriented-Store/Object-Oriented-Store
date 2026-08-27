package com.ohgiraffers.store.member.service;

import com.ohgiraffers.store.member.model.MemberDTO;
import com.ohgiraffers.store.member.repository.MemberDAO;

public class MemberService {

    private final MemberDAO memberDAO = new MemberDAO();

    public boolean joinMember(MemberDTO member){

        if(member == null){
            return false;
        }

        String loginId = member.getLoginId();

        if(loginId == null || loginId.isBlank() || loginId.length() > 25){
            return false;
        }

        if (memberDAO.isLoginIdDuplicate(loginId)){
            return false;
        }

        int result = memberDAO.insertMember(member);

        return result > 0;
    }
}
