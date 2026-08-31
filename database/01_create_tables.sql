-- Object-Oriented Store
-- MySQL 8.x table creation script
-- Run this script after selecting the database to use.

SET NAMES utf8mb4;

USE `object_oriented_store`;

CREATE TABLE `tbl_category` (
    `category_code` INT NOT NULL AUTO_INCREMENT COMMENT '카테고리식별코드',
    `category_name` VARCHAR(30) NOT NULL COMMENT '카테고리이름',
    CONSTRAINT `PK_TBL_CATEGORY` PRIMARY KEY (`category_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '카테고리';

CREATE TABLE `tbl_membership_grade` (
    `grade_code` INT NOT NULL AUTO_INCREMENT COMMENT '멤버십등급식별코드',
    `grade_name` VARCHAR(30) NOT NULL COMMENT '멤버십등급이름',
    `min_purchase_amount` INT NOT NULL COMMENT '등급적용최소구매금액',
    `reward_rate` INT NOT NULL COMMENT '구매금액에적용되는적립률',
    CONSTRAINT `PK_TBL_MEMBERSHIP_GRADE` PRIMARY KEY (`grade_code`),
    CONSTRAINT `CHK_MEMBERSHIP_GRADE_MIN_PURCHASE`
        CHECK (`min_purchase_amount` >= 0),
    CONSTRAINT `CHK_MEMBERSHIP_GRADE_REWARD_RATE`
        CHECK (`reward_rate` BETWEEN 0 AND 100)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '멤버십등급';

CREATE TABLE `tbl_promotion` (
    `promotion_code` INT NOT NULL AUTO_INCREMENT COMMENT '행사식별코드',
    `promotion_name` VARCHAR(100) NOT NULL COMMENT '행사이름',
    `promotion_column` VARCHAR(255) NOT NULL COMMENT '행사내용',
    `discount_value` INT NOT NULL COMMENT '행사에적용되는할인율',
    `promotion_status` VARCHAR(1) NOT NULL COMMENT '행사진행상태',
    CONSTRAINT `PK_TBL_PROMOTION` PRIMARY KEY (`promotion_code`),
    CONSTRAINT `CHK_PROMOTION_DISCOUNT_VALUE`
        CHECK (`discount_value` BETWEEN 0 AND 100),
    CONSTRAINT `CHK_PROMOTION_STATUS`
        CHECK (`promotion_status` IN ('Y', 'N'))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '행사';

CREATE TABLE `tbl_product` (
    `product_code` INT NOT NULL AUTO_INCREMENT COMMENT '상품식별코드',
    `product_name` VARCHAR(255) NOT NULL COMMENT '상품명',
    `product_price` INT NOT NULL COMMENT '상품판매가격',
    `stock_quantity` INT NOT NULL DEFAULT 0 COMMENT '현재재고수량',
    `category_code` INT NOT NULL COMMENT '카테고리식별코드',
    `is_deleted` CHAR(1) NOT NULL DEFAULT 'N' COMMENT '상품삭제여부',
    CONSTRAINT `PK_TBL_PRODUCT` PRIMARY KEY (`product_code`),
    CONSTRAINT `CHK_PRODUCT_PRICE`
        CHECK (`product_price` >= 0),
    CONSTRAINT `CHK_PRODUCT_STOCK_QUANTITY`
        CHECK (`stock_quantity` >= 0),
    CONSTRAINT `CHK_PRODUCT_IS_DELETED`
        CHECK (`is_deleted` IN ('Y', 'N')),
    CONSTRAINT `FK_CATEGORY_TO_PRODUCT`
        FOREIGN KEY (`category_code`)
        REFERENCES `tbl_category` (`category_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '상품';

CREATE TABLE `tbl_member` (
    `member_code` INT NOT NULL AUTO_INCREMENT COMMENT '멤버십코드',
    `grade_code` INT NOT NULL COMMENT '멤버십등급식별코드',
    `login_id` VARCHAR(25) NOT NULL COMMENT '아이디',
    `password` VARCHAR(255) NOT NULL COMMENT '비밀번호',
    `nickname` VARCHAR(25) NOT NULL COMMENT '닉네임',
    `phone` INT NOT NULL COMMENT '휴대폰번호',
    `point_balance` INT NOT NULL DEFAULT 0 COMMENT '보유포인트',
    `total_amount` INT NOT NULL DEFAULT 0 COMMENT '총누적금액',
    CONSTRAINT `PK_TBL_MEMBER` PRIMARY KEY (`member_code`),
    CONSTRAINT `UQ_MEMBER_LOGIN_ID` UNIQUE (`login_id`),
    CONSTRAINT `CHK_MEMBER_POINT_BALANCE`
        CHECK (`point_balance` >= 0),
    CONSTRAINT `CHK_MEMBER_TOTAL_AMOUNT`
        CHECK (`total_amount` >= 0),
    CONSTRAINT `FK_MEMBERSHIP_GRADE_TO_MEMBER`
        FOREIGN KEY (`grade_code`)
        REFERENCES `tbl_membership_grade` (`grade_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '멤버십';

CREATE TABLE `tbl_order` (
    `order_code` INT NOT NULL COMMENT '주문식별번호',
    `member_code` INT NOT NULL COMMENT '멤버십코드',
    `original_amount` INT NOT NULL COMMENT '할인전상품총액',
    `discount_amount` INT NOT NULL DEFAULT 0 COMMENT '행사등을통해할인된금액',
    `final_amount` INT NOT NULL COMMENT '실제결제금액',
    `ordered_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '주문생성일시',
    `order_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '주문진행상태',
    `refunded_at` DATETIME NULL COMMENT '환불완료시간',
    CONSTRAINT `PK_TBL_ORDER` PRIMARY KEY (`order_code`),
    CONSTRAINT `CHK_ORDER_ORIGINAL_AMOUNT`
        CHECK (`original_amount` >= 0),
    CONSTRAINT `CHK_ORDER_DISCOUNT_AMOUNT`
        CHECK (`discount_amount` >= 0),
    CONSTRAINT `CHK_ORDER_FINAL_AMOUNT`
        CHECK (`final_amount` >= 0),
    CONSTRAINT `CHK_ORDER_STATUS`
        CHECK (`order_status` IN ('PENDING', 'PAID', 'CANCELED')),
    CONSTRAINT `FK_MEMBER_TO_ORDER`
        FOREIGN KEY (`member_code`)
        REFERENCES `tbl_member` (`member_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '주문';

CREATE TABLE `tbl_payment` (
    `pay_code` INT NOT NULL AUTO_INCREMENT COMMENT '결제식별코드',
    `order_code` INT NOT NULL COMMENT '주문식별번호',
    `member_code` INT NOT NULL COMMENT '멤버십코드',
    `payment_method` VARCHAR(20) NOT NULL COMMENT '결제방식선택',
    `original_amount` INT NOT NULL COMMENT '할인전상품총액',
    `discount_amount` INT NOT NULL COMMENT '행사등을통해할인된금액',
    `point_use` INT NOT NULL DEFAULT 0 COMMENT '주문에사용한포인트',
    `final_amount` INT NOT NULL COMMENT '실제결제금액',
    `payment_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '결제상태구분',
    CONSTRAINT `PK_TBL_PAYMENT` PRIMARY KEY (`pay_code`),
    CONSTRAINT `UQ_PAYMENT_ORDER_CODE` UNIQUE (`order_code`),
    CONSTRAINT `CHK_PAYMENT_ORIGINAL_AMOUNT`
        CHECK (`original_amount` >= 0),
    CONSTRAINT `CHK_PAYMENT_DISCOUNT_AMOUNT`
        CHECK (`discount_amount` >= 0),
    CONSTRAINT `CHK_PAYMENT_POINT_USE`
        CHECK (`point_use` >= 0),
    CONSTRAINT `CHK_PAYMENT_FINAL_AMOUNT`
        CHECK (`final_amount` >= 0),
    CONSTRAINT `CHK_PAYMENT_STATUS`
        CHECK (`payment_status` IN ('PENDING', 'COMPLETED', 'CANCELED', 'FAILED')),
    CONSTRAINT `FK_ORDER_TO_PAYMENT`
        FOREIGN KEY (`order_code`)
        REFERENCES `tbl_order` (`order_code`),
    CONSTRAINT `FK_MEMBER_TO_PAYMENT`
        FOREIGN KEY (`member_code`)
        REFERENCES `tbl_member` (`member_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '결제';

CREATE TABLE `tbl_promotion_product` (
    `promotion_code` INT NOT NULL COMMENT '행사식별코드',
    `product_code` INT NOT NULL COMMENT '상품식별코드',
    CONSTRAINT `PK_TBL_PROMOTION_PRODUCT`
        PRIMARY KEY (`promotion_code`, `product_code`),
    CONSTRAINT `FK_PROMOTION_TO_PROMOTION_PRODUCT`
        FOREIGN KEY (`promotion_code`)
        REFERENCES `tbl_promotion` (`promotion_code`),
    CONSTRAINT `FK_PRODUCT_TO_PROMOTION_PRODUCT`
        FOREIGN KEY (`product_code`)
        REFERENCES `tbl_product` (`product_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '행사상품';

CREATE TABLE `tbl_order_item` (
    `order_code` INT NOT NULL COMMENT '주문식별번호',
    `product_code` INT NOT NULL COMMENT '상품식별코드',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '상품주문수량',
    CONSTRAINT `PK_TBL_ORDER_ITEM`
        PRIMARY KEY (`order_code`, `product_code`),
    CONSTRAINT `CHK_ORDER_ITEM_QUANTITY`
        CHECK (`quantity` > 0),
    CONSTRAINT `FK_ORDER_TO_ORDER_ITEM`
        FOREIGN KEY (`order_code`)
        REFERENCES `tbl_order` (`order_code`),
    CONSTRAINT `FK_PRODUCT_TO_ORDER_ITEM`
        FOREIGN KEY (`product_code`)
        REFERENCES `tbl_product` (`product_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '주문상세';
