package com.ohgiraffers.store.category.model;

/** tbl_category 한 행의 카테고리코드와 카테고리명을 담는 객체이다. */
public class CategoryDTO {

    private int categoryCode;
    private String categoryName;

    public CategoryDTO() {
    }

    public CategoryDTO(int categoryCode, String categoryName) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
    }

    public int getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(int categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return categoryCode + ". " + categoryName;
    }
}
