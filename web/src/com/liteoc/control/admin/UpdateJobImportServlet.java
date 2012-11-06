package com.liteoc.control.admin;


import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;
import org.springframework.scheduling.quartz.JobDetailBean;

import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.control.SpringServletAccess;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.core.form.StringUtil;
import com.liteoc.dao.login.UserAccountDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;
import com.liteoc.web.SQLInitServlet;
import com.liteoc.web.job.ImportSpringJob;
import com.liteoc.web.job.TriggerService;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UpdateJobImportServlet extends SecureController {

    private static String SCHEDULER = "schedulerFactoryBean";
    private static String TRIGGER_IMPORT_GROUP = "importTrigger";
    private StdScheduler scheduler;
    private SimpleTrigger trigger;
    private JobDataMap dataMap;
    private static final String IMPORT_DIR = SQLInitServlet.getField("filePath") + CreateJobImportServlet.DIR_PATH + File.separator;

    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin() || ub.isTechAdmin()) {
            return;
        }
//        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
//            return;
//        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO

    }

    private StdScheduler getScheduler() {
        scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(context).getBean(SCHEDULER);
        return scheduler;
    }

    private void setUpServlet(Trigger trigger) throws Exception {
        FormProcessor fp2 = new FormProcessor(request);

        request.setAttribute(CreateJobImportServlet.JOB_NAME, trigger.getName());
        request.setAttribute(CreateJobImportServlet.JOB_DESC, trigger.getDescription());

        dataMap = trigger.getJobDataMap();
        String contactEmail = dataMap.getString(ImportSpringJob.EMAIL);
        System.out.println("found email: " + contactEmail);
        int userId = dataMap.getInt(ImportSpringJob.USER_ID);
        int hours = dataMap.getInt(CreateJobImportServlet.HOURS);
        int minutes = dataMap.getInt(CreateJobImportServlet.MINUTES);
        String directory = dataMap.getString(ImportSpringJob.DIRECTORY);
        String studyName = dataMap.getString(ImportSpringJob.STUDY_NAME);

        request.setAttribute(ImportSpringJob.EMAIL, contactEmail);
        request.setAttribute(ImportSpringJob.STUDY_NAME, studyName);
        request.setAttribute("filePath", directory);
        request.setAttribute("firstFilePath", IMPORT_DIR);
        request.setAttribute("hours", Integer.toString(hours));
        request.setAttribute("minutes", Integer.toString(minutes));

        Date jobDate = trigger.getNextFireTime();

        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        StudyDAO sdao = new StudyDAO(sm.getDataSource());

        // ArrayList studies = udao.findStudyByUser(ub.getName(), (ArrayList)
        // sdao.findAll());
        // request.setAttribute("studies", studies);
        ArrayList<StudyBean> all = (ArrayList<StudyBean>) sdao.findAll();
        ArrayList<StudyBean> finalList = new ArrayList<StudyBean>();
        for (StudyBean sb : all) {
            if (!(sb.getParentStudyId() > 0)) {
                finalList.add(sb);
                // System.out.println("found study name: " + sb.getName());
                finalList.addAll(sdao.findAllByParent(sb.getId()));
            }
        }
        // System.out.println("found list of studies: " + finalList.toString());
        addEntityList("studies", finalList, respage.getString("a_user_cannot_be_created_no_study_as_active"), Page.ADMIN_SYSTEM);
        // tbh >>
        // HashMap presetValues = new HashMap();
        // Calendar calendar = new GregorianCalendar();
        // calendar.setTime(jobDate);
        // presetValues.put(CreateJobImportServlet.DATE_START_JOB + "Hour",
        // calendar.get(Calendar.HOUR_OF_DAY));
        // presetValues.put(CreateJobImportServlet.DATE_START_JOB + "Minute",
        // calendar.get(Calendar.MINUTE));
        // // TODO this will have to match l10n formatting
        // presetValues.put(CreateJobImportServlet.DATE_START_JOB + "Date",
        // (calendar.get(Calendar.MONTH) + 1) + "/" +
        // calendar.get(Calendar.DATE) + "/"
        // + calendar.get(Calendar.YEAR));
        // fp2.setPresetValues(presetValues);
        // setPresetValues(fp2.getPresetValues());

    }

    @Override
    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        TriggerService triggerService = new TriggerService();
        String action = fp.getString("action");
        String triggerName = fp.getString("tname");
        scheduler = getScheduler();
        System.out.println("found trigger name " + triggerName);
        Trigger trigger = scheduler.getTrigger(triggerName, TRIGGER_IMPORT_GROUP);
        //System.out.println("found trigger from the other side " + trigger.getFullName());
        if (StringUtil.isBlank(action)) {
            setUpServlet(trigger);
            forwardPage(Page.UPDATE_JOB_IMPORT);
        } else if ("confirmall".equalsIgnoreCase(action)) {
            HashMap errors = triggerService.validateImportJobForm(fp, request, scheduler.getTriggerNames("DEFAULT"), trigger.getName());
            if (!errors.isEmpty()) {
                // send back
                addPageMessage("Your modifications caused an error, please see the messages for more information.");
                setUpServlet(trigger);
                forwardPage(Page.UPDATE_JOB_IMPORT);
            } else {
                StudyDAO studyDAO = new StudyDAO(sm.getDataSource());
                int studyId = fp.getInt(CreateJobImportServlet.STUDY_ID);
                StudyBean study = (StudyBean) studyDAO.findByPK(studyId);
                // in the place of a users' current study, tbh
                Date startDate = trigger.getStartTime();
                trigger = triggerService.generateImportTrigger(fp, sm.getUserBean(), study, startDate, request.getLocale().getLanguage());
                // scheduler = getScheduler();
                JobDetailBean jobDetailBean = new JobDetailBean();
                jobDetailBean.setGroup(TRIGGER_IMPORT_GROUP);
                jobDetailBean.setName(trigger.getName());
                jobDetailBean.setJobClass(com.liteoc.web.job.ImportStatefulJob.class);
                jobDetailBean.setJobDataMap(trigger.getJobDataMap());
                jobDetailBean.setDurability(true); // need durability?
                jobDetailBean.setVolatility(false);

                try {
                    scheduler.deleteJob(triggerName, TRIGGER_IMPORT_GROUP);
                    Date dateStart = scheduler.scheduleJob(jobDetailBean, trigger);

                    addPageMessage("Your job has been successfully modified.");
                    forwardPage(Page.VIEW_IMPORT_JOB_SERVLET);
                } catch (SchedulerException se) {
                    se.printStackTrace();
                    // set a message here with the exception message
                    setUpServlet(trigger);
                    addPageMessage("There was an unspecified error with your creation, please contact an administrator.");
                    forwardPage(Page.UPDATE_JOB_IMPORT);
                }
            }
        }

    }

}