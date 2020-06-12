package com.arematics.minecraft.core.data.repository;

import com.arematics.minecraft.core.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * @author Enrico
 */
public interface DefaultRepository<T, ID> extends Repository<T, ID> {

    List<T> findAll();

    List<T> findAll(Sort sort);

    Optional<T> findById(ID id);

    List<T> findAllById(List<ID> ids);

    boolean existsById(ID id);

    <S extends T> S save(S t);

    <S extends T> List<S> saveAll(List<S> entities);

    void deleteById(ID id);

    void deleteAllById(List<ID> ids);

    void delete(T entity);

    void deleteAll(List<? extends T> entities);

    long count();
}
