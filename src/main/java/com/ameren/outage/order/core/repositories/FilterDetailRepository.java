package com.ameren.outage.order.core.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.ameren.outage.order.core.entities.FilterDetail;

@Repository
public interface FilterDetailRepository extends PagingAndSortingRepository<FilterDetail, Long>{

}
