-- Object-Oriented Store
-- 기존 DB에서 중복 저장되던 product_status 컬럼을 제거하는 1회성 마이그레이션
-- 판매 가능 여부는 product-query.xml이 stock_quantity를 기준으로 계산한다.

USE `object_oriented_store`;

-- 기존 CHECK 제약조건이 있으면 컬럼보다 먼저 제거한다.
SET @product_status_check_exists = (
    SELECT COUNT(*)
      FROM information_schema.table_constraints
     WHERE constraint_schema = DATABASE()
       AND table_name = 'tbl_product'
       AND constraint_name = 'CHK_PRODUCT_STATUS'
       AND constraint_type = 'CHECK'
);

SET @drop_check_sql = IF(
    @product_status_check_exists > 0,
    'ALTER TABLE `tbl_product` DROP CHECK `CHK_PRODUCT_STATUS`',
    'SELECT ''CHK_PRODUCT_STATUS 제약조건이 없습니다.'' AS message'
);

PREPARE drop_check_statement FROM @drop_check_sql;
EXECUTE drop_check_statement;
DEALLOCATE PREPARE drop_check_statement;

-- product_status 컬럼이 있으면 제거한다.
SET @product_status_column_exists = (
    SELECT COUNT(*)
      FROM information_schema.columns
     WHERE table_schema = DATABASE()
       AND table_name = 'tbl_product'
       AND column_name = 'product_status'
);

SET @drop_column_sql = IF(
    @product_status_column_exists > 0,
    'ALTER TABLE `tbl_product` DROP COLUMN `product_status`',
    'SELECT ''product_status 컬럼이 이미 제거되어 있습니다.'' AS message'
);

PREPARE drop_column_statement FROM @drop_column_sql;
EXECUTE drop_column_statement;
DEALLOCATE PREPARE drop_column_statement;
