package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.SubTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTagRepository extends JpaRepository<SubTag, Long> {
}