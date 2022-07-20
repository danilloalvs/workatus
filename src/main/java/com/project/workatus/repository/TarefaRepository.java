package com.project.workatus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.workatus.model.TarefaModel;

public interface TarefaRepository extends JpaRepository<TarefaModel, Integer> {

	@Query("SELECT t FROM tbTarefa t WHERE t.titulo = ?1")
    public TarefaModel findByTitulo(String titulo);
    public TarefaModel findById(int id);
	public void deleteById(int id);
	public TarefaModel findTopByOrderByIdDesc();
}
