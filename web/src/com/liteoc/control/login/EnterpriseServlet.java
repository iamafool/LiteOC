/*
 * Created on Sep 28, 2005
 *
 *
 */
package com.liteoc.control.login;

import com.liteoc.control.core.SecureController;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

/**
 * @author thickerson
 *
 *
 */
public class EnterpriseServlet extends SecureController {

    @Override
    public void mayProceed() throws InsufficientPermissionException {

    }

    @Override
    public void processRequest() throws Exception {
        resetPanel();
        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);
        setToPanel("", "");
        forwardPage(Page.ENTERPRISE);
    }

}
