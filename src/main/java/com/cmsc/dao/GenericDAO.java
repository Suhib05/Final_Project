package com.cmsc.dao;

import java.util.List;

/**
 * Generic CRUD contract implemented by every DAO in the project.
 *
 * @param <T> the entity type this DAO manages
 * @author Suhib
 */
public interface GenericDAO<T> {

    T save(T entity);

    T findById(int id);

    T update(T entity);

    void delete(int id);

    List<T> findAll();
}