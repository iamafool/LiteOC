package com.liteoc.domain.rule.action;

import com.liteoc.domain.Status;
import com.liteoc.domain.enumsupport.CodedEnum;

import java.util.HashMap;

/*
 * Use this enum as operator holder
 * @author Krikor Krumlian
 *
 */

public enum ActionType implements CodedEnum {

    FILE_DISCREPANCY_NOTE(1, "DiscrepancyNoteAction"), EMAIL(2, "EmailAction"), SHOW(3, "ShowAction"), INSERT(4, "InsertAction"), HIDE(5, "HideAction");


    private int code;
    private String description;

    ActionType(int code) {
        this(code, null);
    }

    ActionType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static Status getByName(String name) {
        return Status.valueOf(Status.class, name);
    }

    public static ActionType getByCode(Integer code) {
        HashMap<Integer, ActionType> enumObjects = new HashMap<Integer, ActionType>();
        for (ActionType theEnum : ActionType.values()) {
            enumObjects.put(theEnum.getCode(), theEnum);
        }
        return enumObjects.get(Integer.valueOf(code));
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
