package com.woojin.batchsample.repository;

import com.woojin.batchsample.entity.AfterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeforeRepository extends JpaRepository<AfterEntity, Long> {
}
