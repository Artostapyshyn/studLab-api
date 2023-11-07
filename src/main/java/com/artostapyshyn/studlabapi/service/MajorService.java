package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Major;

import java.util.List;

public interface MajorService {
    Major findByName(String name);

    Major save(Major major);

    List<Major> findAll();
}
