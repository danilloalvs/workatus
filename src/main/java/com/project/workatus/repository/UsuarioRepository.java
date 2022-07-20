package com.project.workatus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.workatus.model.UsuarioModel;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Integer> {

    public UsuarioModel findByLogin(String login);
    public UsuarioModel findById(int id);
	public void deleteById(int id);
}
