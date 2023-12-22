package com.ec.framework.enums;

/**
 * 组内关系枚举
 * @ClassName GroupRelationEnum
 * @Description
 * @Author: LiHao
 * @Since 2023/12/22 21:28
 */
public enum GroupRelationEnum {

    SHARE("share"),
    EXCLUDE("exclude");

    private String type;

    GroupRelationEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
