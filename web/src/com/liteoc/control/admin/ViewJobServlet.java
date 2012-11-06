package com.liteoc.control.admin;


import org.quartz.JobDataMap;
import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.liteoc.bean.admin.TriggerBean;
import com.liteoc.bean.extract.DatasetBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.control.SpringServletAccess;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.dao.extract.DatasetDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.service.extract.XsltTriggerService;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;
import com.liteoc.web.bean.EntityBeanTable;
import com.liteoc.web.bean.TriggerRow;
import com.liteoc.web.job.ExampleSpringJob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 
 * @author thickerson purpose: to generate the list of jobs and allow us to view them
 */
public class ViewJobServlet extends SecureController {

   
    private static String SCHEDULER = "schedulerFactoryBean";
    private static String EXPORT_TRIGGER = "exportTrigger";

    private SchedulerFactoryBean schedulerFactoryBean;
    private StdScheduler scheduler;

    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        // TODO copied from CreateJobExport - DRY? tbh
        if (ub.isSysAdmin() || ub.isTechAdmin()) {
            return;
        }
//        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {// ?
//            // ?
//            return;
//        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO
        // above copied from create dataset servlet, needs to be changed to
        // allow only admin-level users

    }

    private StdScheduler getScheduler() {
        scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(context).getBean(SCHEDULER);
        return scheduler;
    }

    @Override
    protected void processRequest() throws Exception {
        // TODO single stage servlet where we get the list of jobs
        // and push them out to the JSP page
        // related classes will be required to generate the table rows
        // and eventually links to view and edit the jobs as well
        FormProcessor fp = new FormProcessor(request);
        // First we must get a reference to a scheduler
        scheduler = getScheduler();
        XsltTriggerService xsltTriggerSrvc = new XsltTriggerService();
        // Scheduler sched = sfb.getScheduler();

     String[] triggerNames = scheduler.getTriggerNames(xsltTriggerSrvc.getTriggerGroupNameForExportJobs());
  //      String[]    triggerNames          =           scheduler.getJobNames(XsltTriggerService.TRIGGER_GROUP_NAME);
        // logger.info("trigger list: "+triggerNames.length);
        // logger.info("trigger names: "+triggerNames.toString());


        ArrayList triggerBeans = new ArrayList();
        for (String triggerName : triggerNames) {
            Trigger trigger = scheduler.getTrigger(triggerName, xsltTriggerSrvc.getTriggerGroupNameForExportJobs());
            try {
                logger.debug("prev fire time " + trigger.getPreviousFireTime().toString());
                logger.debug("next fire time " + trigger.getNextFireTime().toString());
                logger.debug("final fire time: " + trigger.getFinalFireTime().toString());
            } catch (NullPointerException npe) {
                // could be nulls in the dates, etc
            }

            // logger.info(trigger.getDescription());
            // logger.info("");//getJobDataMap()
            TriggerBean triggerBean = new TriggerBean();
            triggerBean.setFullName(trigger.getName());
            triggerBean.setPreviousDate(trigger.getPreviousFireTime());
            triggerBean.setNextDate(trigger.getNextFireTime());
            if (trigger.getDescription() != null) {
                triggerBean.setDescription(trigger.getDescription());
            }
            // setting: frequency, dataset name
            JobDataMap dataMap = new JobDataMap();
            DatasetDAO datasetDAO = new DatasetDAO(sm.getDataSource());
            StudyDAO studyDao = new StudyDAO(sm.getDataSource());
            if (trigger.getJobDataMap().size() > 0) {
                dataMap = trigger.getJobDataMap();
                int dsId = dataMap.getInt(ExampleSpringJob.DATASET_ID);
                String periodToRun = dataMap.getString(ExampleSpringJob.PERIOD);
                triggerBean.setPeriodToRun(periodToRun);
                DatasetBean dataset = (DatasetBean) datasetDAO.findByPK(dsId);
                triggerBean.setDataset(dataset);
                triggerBean.setDatasetName(dataset.getName());
                StudyBean study = (StudyBean) studyDao.findByPK(dataset.getStudyId());
                triggerBean.setStudyName(study.getName());
                // triggerBean.setStudyName(dataMap.getString(ExampleSpringJob.STUDY_NAME));
            }
            logger.debug("Trigger Priority: " + trigger.getName() + " " + trigger.getPriority());
            if (scheduler.getTriggerState(triggerName, XsltTriggerService.TRIGGER_GROUP_NAME) == Trigger.STATE_PAUSED) {
                triggerBean.setActive(false);
                logger.debug("setting active to false for trigger: " + trigger.getName());
            } else {
                triggerBean.setActive(true);
                logger.debug("setting active to TRUE for trigger: " + trigger.getName());
            }
            triggerBeans.add(triggerBean);
            // our wrapper to show triggers
        }

        ArrayList allRows = TriggerRow.generateRowsFromBeans(triggerBeans);

        EntityBeanTable table = fp.getEntityBeanTable();
        String[] columns =
            { resword.getString("name"), resword.getString("previous_fire_time"), resword.getString("next_fire_time"), resword.getString("description"),
                resword.getString("period_to_run"), resword.getString("dataset"), resword.getString("study"), resword.getString("actions") };
        table.setColumns(new ArrayList(Arrays.asList(columns)));
        table.hideColumnLink(3);
        table.hideColumnLink(7);
        table.setQuery("ViewJob", new HashMap());
        // table.addLink("", "CreateUserAccount");
        table.setSortingColumnInd(0);
        table.setRows(allRows);
        table.computeDisplay();

        request.setAttribute("table", table);
        // throw new NullPointerException("faking an error here");
        forwardPage(Page.VIEW_JOB);

    }

}
