package com.liteoc.service;

import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.exception.OpenClinicaSystemException;

import java.util.Date;
import java.util.HashMap;

public interface EventServiceInterface {

    public HashMap<String, String> scheduleEvent(UserAccountBean user, Date startDateTime, Date endDateTime, String location, String studyUniqueId,
            String siteUniqueId, String eventDefinitionOID, String studySubjectId) throws OpenClinicaSystemException;

}