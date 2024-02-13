package com.example.devkorproject.alarm.repository;

import com.example.devkorproject.alarm.entity.AlarmEntity;
import com.example.devkorproject.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<AlarmEntity,Long> {
    List<AlarmEntity> findTop20ByCustomer_CustomerIdOrderByDateDesc(Long customerId);

}
