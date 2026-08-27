-- Object-Oriented Store
-- MySQL 8.x initial seed data

USE `object_oriented_store`;

-- 회원가입과 등급 자동 변경에 사용하는 기본 회원등급
INSERT INTO `tbl_membership_grade` (
    `grade_code`,
    `grade_name`,
    `min_purchase_amount`,
    `reward_rate`
) VALUES
    (1, 'BASIC', 0, 0),
    (2, 'GOLD', 50000, 10),
    (3, 'VIP', 200000, 50)
ON DUPLICATE KEY UPDATE
    `grade_name` = VALUES(`grade_name`),
    `min_purchase_amount` = VALUES(`min_purchase_amount`),
    `reward_rate` = VALUES(`reward_rate`);

-- 상품 등록과 카테고리별 조회에 사용하는 기본 카테고리
INSERT INTO `tbl_category` (
    `category_code`,
    `category_name`
) VALUES
    (1, '행사제품'),
    (2, '라면류'),
    (3, '과자류'),
    (4, '간편식품'),
    (5, '신선제품'),
    (6, '음료'),
    (7, '아이스크림'),
    (8, '생활용품'),
    (9, '주류'),
    (10, '담배')
ON DUPLICATE KEY UPDATE
    `category_name` = VALUES(`category_name`);
