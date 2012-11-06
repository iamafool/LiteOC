package com.liteoc.bean.extract;

import com.liteoc.bean.core.EntityBean;

public class FilterDataBean extends EntityBean {
	   	private String subjectId;
	   	private String studyEventName;
	   	private String value;
	   	private int itemId;
	   	private int eventCRFId;
	   	
		public int getItemId() {
			return itemId;
		}
		public void setItemId(int itemId) {
			this.itemId = itemId;
		}
		public int getEventCRFId() {
			return eventCRFId;
		}
		public void setEventCRFId(int eventCRFId) {
			this.eventCRFId = eventCRFId;
		}
		public String getSubjectId() {
			return subjectId;
		}
		public void setSubjectId(String subjectId) {
			this.subjectId = subjectId;
		}
		public String getStudyEventName() {
			return studyEventName;
		}
		public void setStudyEventName(String studyEventName) {
			this.studyEventName = studyEventName;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}

}
