/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.submit;


import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.control.SpringServletAccess;
import com.liteoc.control.core.SecureController;
import com.liteoc.core.SecurityManager;
import com.liteoc.web.InsufficientPermissionException;

/**
 * Checks user's password with the one int the session
 * 
 * @author shamim
 * 
 */
public class MatchPasswordServlet extends SecureController {
    @Override
    protected void processRequest() throws Exception {
        String password = request.getParameter("password");
        logger.info("password [" + password + "]");
        if (password != null && !password.equals("")) {
            SecurityManager securityManager = ((SecurityManager) SpringServletAccess.getApplicationContext(context).getBean("securityManager"));
            //String encodedUserPass = com.liteoc.core.SecurityManager.getInstance().encrytPassword(password);
            UserAccountBean ub = (UserAccountBean) session.getAttribute("userBean");
            //logger.info("session pass[" + ub.getPasswd() + "]");
            //logger.info("user pass[" + encodedUserPass + "]");
            if (securityManager.isPasswordValid(ub.getPasswd(), password, getUserDetails())) {
                response.getWriter().print("true");
            } else {
                response.getWriter().print("false");
            }
            return;
        }
    }

    @Override
    protected void mayProceed() throws InsufficientPermissionException {
    }
}
