package com.johann.binarytea.hibernate.repository;

import com.johann.binarytea.hibernate.model.MenuItem;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * HibernateDaoSupport 辅助类是一个方便的超类，用于简化 Hibernate 数据访问代码。
 * 它包含一个 HibernateTemplate，可以用于管理 Hibernate Session 的创建和关闭。
 * 通过继承 HibernateDaoSupport，可以将 HibernateTemplate 注入到数据访问对象中。
 *
 * HibernateDaoSupport 与 HibernateTemplate 的区别在于：
 * HibernateTemplate 是一个模板类，它封装了所有的 Hibernate 操作，包括创建和关闭 Session，加载对象，保存对象，删除对象，查询对象等。
 * HibernateDaoSupport 是一个抽象类，它提供了一个 HibernateTemplate 对象，用于简化 Hibernate 数据访问代码。
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Repository
// @Transactional 事务一般加在 Service 层上
@Transactional
public class MenuRepositoryHibernate extends HibernateDaoSupport {

    /**
     * 通过构造函数注入 SessionFactory
     * @param sessionFactory
     */
    public MenuRepositoryHibernate(SessionFactory sessionFactory){
        super.setSessionFactory(sessionFactory);
    }

    /**
     * 获取菜单项数量
     * @return
     */
    public long countMenuItems() {
        // 使用 HQL 查询语言
        return getHibernateTemplate().execute(session ->
                session.createQuery("select count(m) from MenuItem m", Long.class).uniqueResult());
    }

    public long countMenuItems2() {
        // 使用 HQL 查询语言
        return getSessionFactory().getCurrentSession()
                .createQuery("select count(m) from MenuItem m",Long.class).getSingleResult();
    }

    /**
     * 查询所有的菜单项
     * @return
     */
    public List<MenuItem> queryAllItems(){
        return getHibernateTemplate().loadAll(MenuItem.class);
    }

    /**
     * 根据 id 获取菜单项
     * @param id
     * @return
     */
    public MenuItem queryForItem(Long id){
        return getHibernateTemplate().get(MenuItem.class,id);
    }

    /**
     * 插入菜单项
     * @param menuItem
     */
    public void insertItem(MenuItem menuItem){
        getHibernateTemplate().save(menuItem);
    }

    /**
     * 更新菜单项
     * @param item
     */
    public void updateItem(MenuItem item) {
        getHibernateTemplate().update(item);
    }

    /**
     * 删除菜单项
     * @param id
     */
    public void deleteItem(Long id) {
        getHibernateTemplate().delete(Objects.requireNonNull(queryForItem(id)));
    }

    /**
     * TODO
     * 1，getHibernateTemplate().findByCriteria()
     * 2，getHibernateTemplate().findByExample()
     */



}
