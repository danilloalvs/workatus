package com.project.workatus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.workatus.model.PostagemModel;

public interface PostagemRepository extends JpaRepository<PostagemModel, Integer> {

    public PostagemModel findById(int id);
	public void deleteById(int id);
}
