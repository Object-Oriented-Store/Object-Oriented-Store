package com.ohgiraffers.store.maincontroller;

//import com.ohgiraffers.store.promotion.repository.Membership;
import com.ohgiraffers.store.member.model.Membership;
import com.ohgiraffers.store.promotion.controller.PromotionRun;
import com.ohgiraffers.store.promotion.service.SettingsOnlyManager;

import java.util.Scanner;

public class MainRun {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        //각 클래스에 스캐너 전달
        SettingsOnlyManager settingsOnlyManager = new SettingsOnlyManager(sc);
        PromotionRun promotionRun = new PromotionRun(sc);
        Membership membership = new Membership(sc);
        Controller cl =  new Controller(sc);

        String userName = cl.Start(); //시작화면 메소드 호출
        cl.SelectCategory(userName);
    }
}
