/*
 * Created on Sep 1, 2005
 *
 *
 */
package com.liteoc.logic.core;

import com.liteoc.bean.core.EntityBean;

/**
 * @author thickerson
 *
 *
 */
public interface BusinessRule {
    public abstract boolean isPropertyTrue(String s);

    public abstract EntityBean doAction(EntityBean o);
}
