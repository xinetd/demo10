package com.example.demo10.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.demo10.model.EntityX;

@RepositoryRestResource
public interface EntityXRepository extends CrudRepository<EntityX, Long> {

}
