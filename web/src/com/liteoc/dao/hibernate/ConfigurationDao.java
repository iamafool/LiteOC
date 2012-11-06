package com.liteoc.dao.hibernate;

import org.springframework.transaction.annotation.Transactional;

import com.liteoc.domain.technicaladmin.ConfigurationBean;

import java.util.ArrayList;

public class ConfigurationDao extends AbstractDomainDao<ConfigurationBean> {

    @Override
    public Class<ConfigurationBean> domainClass() {
        return ConfigurationBean.class;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ConfigurationBean> findAll() {
        String query = "from " + getDomainClassName();
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        return (ArrayList<ConfigurationBean>) q.list();
    }

    @Transactional
    public ConfigurationBean findByKey(String key) {
        String query = "from " + getDomainClassName() + " do where do.key = :key  ";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setString("key", key);
        return (ConfigurationBean) q.uniqueResult();
    }

}
