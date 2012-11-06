package com.liteoc.core;

import org.apache.commons.dbcp.BasicDataSource;

public class ExtendedBasicDataSource extends BasicDataSource {

    public void setBigStringTryClob(String value) {
        addConnectionProperty("SetBigStringTryClob", value);
    }
}