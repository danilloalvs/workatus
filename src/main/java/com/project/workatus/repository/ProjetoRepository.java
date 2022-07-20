package com.project.workatus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.workatus.model.ProjetoModel;

public interface ProjetoRepository extends JpaRepository<ProjetoModel, Integer> {

    public ProjetoModel findByNome(String nome);
    public ProjetoModel findById(int id);
	public void deleteById(int id);
}
