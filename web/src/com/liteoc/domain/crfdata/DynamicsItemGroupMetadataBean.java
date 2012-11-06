package com.liteoc.domain.crfdata;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.ItemGroupMetadataBean;
import com.liteoc.domain.AbstractMutableDomainObject;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dyn_item_group_metadata")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "dyn_item_group_metadata_id_seq") })
public class DynamicsItemGroupMetadataBean extends AbstractMutableDomainObject {

    private boolean showGroup;
    private int eventCrfId;
    private int itemGroupMetadataId;
    private int itemGroupId;
    private int passedDde;

    public DynamicsItemGroupMetadataBean() {
        passedDde = 0;
    }

    public DynamicsItemGroupMetadataBean(ItemGroupMetadataBean metadataBean, EventCRFBean eventCrfBean) {
        setEventCrfId(eventCrfBean.getId());
        setItemGroupMetadataId(metadataBean.getId());
        setItemGroupId(metadataBean.getItemGroupId());
        setShowGroup(metadataBean.isShowGroup());
        setPassedDde(0);
    }

    public boolean isShowGroup() {
        return showGroup;
    }

    public void setShowGroup(boolean showGroup) {
        this.showGroup = showGroup;
    }

    public int getPassedDde() {
        return passedDde;
    }

    public void setPassedDde(int passedDde) {
        this.passedDde = passedDde;
    }

    public int getEventCrfId() {
        return eventCrfId;
    }

    public void setEventCrfId(int eventCrfId) {
        this.eventCrfId = eventCrfId;
    }

    public int getItemGroupMetadataId() {
        return itemGroupMetadataId;
    }

    public void setItemGroupMetadataId(int itemGroupMetadataId) {
        this.itemGroupMetadataId = itemGroupMetadataId;
    }

    public int getItemGroupId() {
        return itemGroupId;
    }

    public void setItemGroupId(int itemGroupId) {
        this.itemGroupId = itemGroupId;
    }

}
