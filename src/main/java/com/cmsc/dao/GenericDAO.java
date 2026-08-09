package com.cmsc.dao;

import java.util.List;

/**
 * Generic CRUD contract implemented by every DAO in the project.
 *
 * @param <T> the entity type this DAO manages
 * @author Suhib
 */
public interface GenericDAO<T> {

    /**
     * Saves a new entity.
     *
     * @param entity entity to save
     * @return saved entity
     */
    T save(T entity);

    /**
     * Finds an entity by identifier.
     *
     * @param id database identifier
     * @return matching entity, or null when absent
     */
    T findById(int id);

    /**
     * Updates an existing entity.
     *
     * @param entity entity containing updated values
     * @return updated entity
     */
    T update(T entity);

    /**
     * Deletes an entity by identifier.
     *
     * @param id database identifier
     */
    void delete(int id);

    /**
     * Retrieves every entity of this type.
     *
     * @return all entities
     */
    List<T> findAll();
}
