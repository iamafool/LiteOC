/*
 * Created on Sep 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.liteoc.logic.masking.rules;

import com.liteoc.bean.core.EntityBean;
import com.liteoc.bean.submit.SubjectBean;
import com.liteoc.logic.core.BusinessRule;

/**
 * @author thickerson
 *
 *
 */
public class MaskSubjectDOBRule implements BusinessRule {
    public boolean isPropertyTrue(String s) {
        if (s.equals(this.getClass().getName())) {
            return true;
        } else {
            return false;
        }
    }

    public EntityBean doAction(EntityBean sb) {
        // cast to a subject bean
        SubjectBean ssb = (SubjectBean) sb;
        ssb.setDateOfBirth(null);// effectively xx-xx-xxxx
        return sb;
    }

}
