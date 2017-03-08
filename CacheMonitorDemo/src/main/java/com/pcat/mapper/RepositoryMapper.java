package com.pcat.mapper;

import java.util.List;

import com.pcat.entity.Repository;

public interface RepositoryMapper {

    List<Repository> selectRepository(long id);
}