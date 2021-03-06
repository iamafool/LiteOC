package com.liteoc.bean.submit;

import com.liteoc.bean.core.AuditableEntityBean;
import com.liteoc.bean.oid.ItemGroupOidGenerator;
import com.liteoc.bean.oid.OidGenerator;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: bruceperry Date: May 7, 2007
 */
public class ItemGroupBean extends AuditableEntityBean {

    private Integer crfId = 0;
    private ItemGroupMetadataBean meta = new ItemGroupMetadataBean();
    private ArrayList itemGroupMetaBeans = new ArrayList();
    // change 07-08-07, tbh

    private String oid;
    private OidGenerator oidGenerator;

    public ItemGroupBean() {
        super();
        crfId = 0;
        name = "";
        meta = new ItemGroupMetadataBean();
        oidGenerator = new ItemGroupOidGenerator();
    }

    /**
     * @return the crfId
     */
    public Integer getCrfId() {
        return crfId;
    }

    /**
     * @param crfId
     *            the crfId to set
     */
    public void setCrfId(Integer crfId) {
        this.crfId = crfId;
    }

    /**
     * @return the meta
     */
    public ItemGroupMetadataBean getMeta() {
        return meta;
    }

    /**
     * @param meta
     *            the meta to set
     */
    public void setMeta(ItemGroupMetadataBean meta) {
        this.meta = meta;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public OidGenerator getOidGenerator() {
        return oidGenerator;
    }

    public void setOidGenerator(OidGenerator oidGenerator) {
        this.oidGenerator = oidGenerator;
    }

    public ArrayList getItemGroupMetaBeans() {
        return itemGroupMetaBeans;
    }

    public void setItemGroupMetaBeans(ArrayList itemGroupMetaBeans) {
        this.itemGroupMetaBeans = itemGroupMetaBeans;
    }
}
