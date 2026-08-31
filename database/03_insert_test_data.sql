-- Object-Oriented Store
-- MySQL 8.x development test data
-- Run 02_insert_seed_data.sql before this script.

USE `object_oriented_store`;

START TRANSACTION;

-- 행사
INSERT INTO `tbl_promotion` (
    `promotion_code`,
    `promotion_name`,
    `promotion_column`,
    `discount_value`,
    `promotion_status`
) VALUES
    (1, '음료 할인 행사', '행사 대상 음료를 10% 할인합니다.', 10, 'Y'),
    (2, '간편식 할인 행사', '행사 대상 간편식품을 20% 할인합니다.', 20, 'Y'),
    (3, '과자 할인 종료 행사', '종료된 과자 할인 행사입니다.', 15, 'N')
ON DUPLICATE KEY UPDATE
    `promotion_name` = VALUES(`promotion_name`),
    `promotion_column` = VALUES(`promotion_column`),
    `discount_value` = VALUES(`discount_value`),
    `promotion_status` = VALUES(`promotion_status`);

-- 상품
INSERT INTO `tbl_product` (
    `product_code`,
    `product_name`,
    `product_price`,
    `stock_quantity`,
    `category_code`
) VALUES
    -- 행사제품
    (1, '생수 세트', 2000, 30, 1),
    (2, '컵라면 세트', 3000, 30, 1),
    (3, '간식 묶음 세트', 5000, 20, 1),
    (4, '도시락 음료 세트', 6500, 20, 1),
    (5, '여행용 생활용품 세트', 8000, 15, 1),
    -- 라면류
    (6, '신라면', 1000, 100, 2),
    (7, '진라면 매운맛', 950, 90, 2),
    (8, '너구리', 1100, 80, 2),
    (9, '짜파게티', 1200, 70, 2),
    (10, '육개장 사발면', 1300, 60, 2),
    -- 과자류
    (11, '새우깡', 1500, 80, 3),
    (12, '포카칩', 1700, 70, 3),
    (13, '초코파이', 4800, 50, 3),
    (14, '꼬깔콘', 1600, 60, 3),
    (15, '홈런볼', 2000, 40, 3),
    -- 간편식품
    (16, '참치 삼각김밥', 1200, 50, 4),
    (17, '전주비빔 삼각김밥', 1300, 45, 4),
    (18, '제육 도시락', 5500, 30, 4),
    (19, '김치볶음밥', 4500, 25, 4),
    (20, '핫바', 2000, 40, 4),
    -- 신선제품
    (21, '바나나', 2000, 20, 5),
    (22, '사과', 1500, 20, 5),
    (23, '샐러드', 4500, 15, 5),
    (24, '구운계란 2입', 1800, 30, 5),
    (25, '딸기컵', 3500, 15, 5),
    -- 음료
    (26, '생수 500ml', 1000, 200, 6),
    (27, '콜라 500ml', 2000, 100, 6),
    (28, '사이다 500ml', 2000, 90, 6),
    (29, '오렌지주스', 2500, 60, 6),
    (30, '아메리카노', 1800, 70, 6),
    -- 아이스크림
    (31, '초코 아이스크림', 1800, 60, 7),
    (32, '바닐라콘', 2000, 50, 7),
    (33, '메로나', 1200, 80, 7),
    (34, '월드콘', 2200, 45, 7),
    (35, '아이스컵', 1000, 100, 7),
    -- 생활용품
    (36, '물티슈', 3000, 40, 8),
    (37, '칫솔', 2500, 35, 8),
    (38, '치약', 3500, 30, 8),
    (39, '건전지', 4000, 25, 8),
    (40, '우산', 8000, 20, 8),
    -- 주류
    (41, '캔맥주', 3500, 50, 9),
    (42, '소주', 2000, 60, 9),
    (43, '발렌타인10년', 50000, 30, 9),
    (44, '막걸리', 1500, 15, 9),
    (45, '발렌타인30년', 200000, 25, 9),
    -- 담배
    (46, '담배 A', 4500, 40, 10),
    (47, '담배 B', 4500, 35, 10),
    (48, '담배 C', 4500, 30, 10),
    (49, '담배 D', 4500, 25, 10),
    (50, '품절 담배 테스트', 4500, 0, 10)
ON DUPLICATE KEY UPDATE
    `product_name` = VALUES(`product_name`),
    `product_price` = VALUES(`product_price`),
    `stock_quantity` = VALUES(`stock_quantity`),
    `category_code` = VALUES(`category_code`),
    `is_deleted` = 'N';

-- 테스트 회원
-- 비밀번호는 개발용 가짜 데이터이며 실제 서비스 비밀번호가 아닙니다.
INSERT INTO `tbl_member` (
    `member_code`,
    `grade_code`,
    `login_id`,
    `password`,
    `nickname`,
    `phone`,
    `point_balance`,
    `total_amount`
) VALUES
    (1, 1, 'basic_member', 'test1234', '베이직회원', 12345678, 0, 0),
    (2, 2, 'gold_member', 'test1234', '골드회원', 23456789, 5000, 50000),
    (3, 3, 'vip_member', 'test1234', '브이아이피회원', 34567890, 50000, 200000)
ON DUPLICATE KEY UPDATE
    `grade_code` = VALUES(`grade_code`),
    `login_id` = VALUES(`login_id`),
    `password` = VALUES(`password`),
    `nickname` = VALUES(`nickname`),
    `phone` = VALUES(`phone`),
    `point_balance` = VALUES(`point_balance`),
    `total_amount` = VALUES(`total_amount`);

-- 관리자 화면 분기 확인에 사용하는 개발용 관리자 계정
-- member_code는 AUTO_INCREMENT에 맡기고 login_id의 UNIQUE 제약으로 중복을 방지합니다.
INSERT INTO `tbl_member` (
    `grade_code`,
    `login_id`,
    `password`,
    `nickname`,
    `phone`,
    `point_balance`,
    `total_amount`
) VALUES (
    1,
    'admin',
    'admin1234',
    '관리자',
    99999999,
    0,
    0
)
ON DUPLICATE KEY UPDATE
    `grade_code` = VALUES(`grade_code`),
    `password` = VALUES(`password`),
    `nickname` = VALUES(`nickname`),
    `phone` = VALUES(`phone`);

-- 테스트 주문
-- 주문번호는 팀 규칙인 월일시분초(MMddHHmmss) 형식의 INT 값입니다.
INSERT INTO `tbl_order` (
    `order_code`,
    `member_code`,
    `original_amount`,
    `discount_amount`,
    `final_amount`,
    `ordered_at`,
    `order_status`,
    `refunded_at`
) VALUES
    (1227100001, 1, 6000, 1000, 4500, '2026-12-27 10:00:01', 'PAID', NULL),
    (1227100501, 2, 8500, 500, 8000, '2026-12-27 10:05:01', 'PENDING', NULL),
    (1227101001, 3, 6000, 0, 5000, '2026-12-27 10:10:01', 'CANCELED', '2026-12-27 10:20:00')
ON DUPLICATE KEY UPDATE
    `member_code` = VALUES(`member_code`),
    `original_amount` = VALUES(`original_amount`),
    `discount_amount` = VALUES(`discount_amount`),
    `final_amount` = VALUES(`final_amount`),
    `ordered_at` = VALUES(`ordered_at`),
    `order_status` = VALUES(`order_status`),
    `refunded_at` = VALUES(`refunded_at`);

-- 테스트 결제
INSERT INTO `tbl_payment` (
    `pay_code`,
    `order_code`,
    `member_code`,
    `payment_method`,
    `original_amount`,
    `discount_amount`,
    `point_use`,
    `final_amount`,
    `payment_status`
) VALUES
    (1, 1227100001, 1, 'CARD', 6000, 1000, 500, 4500, 'COMPLETED'),
    (2, 1227100501, 2, 'KAKAO_PAY', 8500, 500, 0, 8000, 'PENDING'),
    (3, 1227101001, 3, 'MOBILE', 6000, 0, 1000, 5000, 'CANCELED')
ON DUPLICATE KEY UPDATE
    `order_code` = VALUES(`order_code`),
    `member_code` = VALUES(`member_code`),
    `payment_method` = VALUES(`payment_method`),
    `original_amount` = VALUES(`original_amount`),
    `discount_amount` = VALUES(`discount_amount`),
    `point_use` = VALUES(`point_use`),
    `final_amount` = VALUES(`final_amount`),
    `payment_status` = VALUES(`payment_status`);

-- 행사와 상품의 연결 데이터
DELETE FROM `tbl_promotion_product`
WHERE `promotion_code` IN (1, 2, 3);

INSERT INTO `tbl_promotion_product` (
    `promotion_code`,
    `product_code`
) VALUES
    (1, 1),
    (1, 26),
    (1, 27),
    (1, 28),
    (2, 16),
    (2, 18),
    (2, 19),
    (3, 11),
    (3, 12)
ON DUPLICATE KEY UPDATE
    `promotion_code` = VALUES(`promotion_code`),
    `product_code` = VALUES(`product_code`);

-- 주문에 포함된 상품과 수량
DELETE FROM `tbl_order_item`
WHERE `order_code` IN (1227100001, 1227100501, 1227101001);

INSERT INTO `tbl_order_item` (
    `order_code`,
    `product_code`,
    `quantity`
) VALUES
    (1227100001, 6, 2),
    (1227100001, 27, 2),
    (1227100501, 18, 1),
    (1227100501, 36, 1),
    (1227101001, 21, 2),
    (1227101001, 26, 2)
ON DUPLICATE KEY UPDATE
    `quantity` = VALUES(`quantity`);

COMMIT;
