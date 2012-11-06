package com.liteoc.service.subject;

import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.managestudy.StudySubjectBean;
import com.liteoc.bean.submit.SubjectBean;

import java.util.Date;
import java.util.List;

public interface SubjectServiceInterface {

    public abstract String createSubject(SubjectBean subjectBean, StudyBean studyBean, Date enrollmentDate, String secondaryId);

    public List<StudySubjectBean> getStudySubject(StudyBean study);

}