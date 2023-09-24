package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.SubTag;
import com.artostapyshyn.studlabapi.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubTagRepository extends JpaRepository<SubTag, Long> {
    Optional<SubTag> findByNameAndTag(String name, Tag tag);
}