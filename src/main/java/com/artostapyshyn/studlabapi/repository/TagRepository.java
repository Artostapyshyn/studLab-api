package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String name);

    Set<Tag> findAllByNameIn(Set<String> names);
}