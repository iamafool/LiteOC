package com.liteoc.service.crfdata;

import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.bean.submit.ItemFormMetadataBean;
import com.liteoc.bean.submit.ItemGroupMetadataBean;

/**
 * MetadataServiceInterface, our abstract interface for Dynamics
 * @author thickerson, Mar 3rd, 2010
 * initial methods: isShown, show and hide
 * (can add others later to enable/disable/color/uncolor, etc
 * initial implementations: ItemMetadataService and GroupMetadataService
 *
 */
public interface MetadataServiceInterface {

    public abstract boolean isShown(Object metadataBean, EventCRFBean eventCrfBean);

    public abstract boolean hide(Object metadataBean, EventCRFBean eventCrfBean);

    public abstract boolean showItem(ItemFormMetadataBean metadataBean, EventCRFBean eventCrfBean, ItemDataBean itemDataBean);

    public abstract boolean showGroup(ItemGroupMetadataBean metadataBean, EventCRFBean eventCrfBean);
}
