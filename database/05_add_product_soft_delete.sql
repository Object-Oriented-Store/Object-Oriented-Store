-- 기존 object_oriented_store DB에 상품 논리 삭제 컬럼을 추가한다.
-- 이미 컬럼과 제약조건이 있으면 다시 실행해도 건너뛴다.

USE `object_oriented_store`;

SET @product_deleted_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'tbl_product'
      AND COLUMN_NAME = 'is_deleted'
);

SET @add_product_deleted_column = IF(
    @product_deleted_column_exists = 0,
    'ALTER TABLE tbl_product ADD COLUMN is_deleted CHAR(1) NOT NULL DEFAULT ''N'' COMMENT ''상품삭제여부''',
    'SELECT ''tbl_product.is_deleted already exists'''
);

PREPARE add_product_deleted_column_statement
    FROM @add_product_deleted_column;
EXECUTE add_product_deleted_column_statement;
DEALLOCATE PREPARE add_product_deleted_column_statement;

SET @product_deleted_check_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'tbl_product'
      AND CONSTRAINT_NAME = 'CHK_PRODUCT_IS_DELETED'
);

SET @add_product_deleted_check = IF(
    @product_deleted_check_exists = 0,
    'ALTER TABLE tbl_product ADD CONSTRAINT CHK_PRODUCT_IS_DELETED CHECK (is_deleted IN (''Y'', ''N''))',
    'SELECT ''CHK_PRODUCT_IS_DELETED already exists'''
);

PREPARE add_product_deleted_check_statement
    FROM @add_product_deleted_check;
EXECUTE add_product_deleted_check_statement;
DEALLOCATE PREPARE add_product_deleted_check_statement;

