package com.liteoc.control;


import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.control.core.CoreSecureController;
import com.liteoc.control.core.SecureController;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSession;

/**
 * Created by A. Hamid.
  * Date: Apr 20, 2010
 * Time: 7:35:04 PM
 */
public class OCSessionListener implements HttpSessionListener

{
   public void sessionCreated(HttpSessionEvent arg0)
  {
     //this will be called when session is created

  }

  public void sessionDestroyed(HttpSessionEvent arg0)
  {
      //This method will be called just before session is to be destroyed
      HttpSession session = arg0.getSession();
      UserAccountBean ub = (UserAccountBean)session.getAttribute("userBean");
      if(ub!=null){
        SecureController.removeLockedCRF(ub.getId());    
        CoreSecureController.removeLockedCRF(ub.getId());    
      }
//      System.out.println("=========================="+ub.getName());
  }
}

