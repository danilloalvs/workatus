package com.project.workatus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.workatus.model.PostagemModel;
import com.project.workatus.model.TarefaModel;
import com.project.workatus.model.UsuarioModel;
import com.project.workatus.model.enums.EnumStatus;
import com.project.workatus.repository.PostagemRepository;
import com.project.workatus.repository.TarefaRepository;
import com.project.workatus.repository.UsuarioRepository;

import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/postagem")
public class PostagemController {

	private final PostagemRepository repository;
	private final TarefaRepository repositoryTarefa;
	private final UsuarioRepository repositoryUsuario;

	public PostagemController(PostagemRepository repository, TarefaRepository repositoryTarefa, UsuarioRepository repositoryUsuario) {
		this.repository = repository;
		this.repositoryTarefa = repositoryTarefa;
		this.repositoryUsuario = repositoryUsuario;
	}

	@ApiOperation(value = "Retorna todos as postagens cadastradas")
	@GetMapping
	public List<PostagemModel> getAll() {
		return repository.findAll();
	}

	@ApiOperation(value = "Retorna uma postagem de acordo com o Id")
	@GetMapping("/id")
	public PostagemModel getPostagemId(@RequestParam int id) {
		return repository.findById(id);
	}

	@ApiOperation(value = "Insere uma postagem no sistema")
	@PostMapping
	public PostagemModel insertPostagem(@RequestBody PostagemModel postagem) {
		boolean idTarefaExiste = idTarefaValido(postagem.getTarefa().getId());
		boolean idUsuarioExiste = idUsuarioValido(postagem.getUsuario().getId());

		if (!idTarefaExiste || !idUsuarioExiste || postagem.getTarefa().getStatus().equals(null)) {
			return null;
		} else {
			Optional<TarefaModel> tarefaOpt = repositoryTarefa.findById(postagem.getTarefa().getId());			
			TarefaModel tarefa = tarefaOpt.get();
			
			if(!postagem.getUsuario().getId().equals(tarefa.getUsuarioFuncionario().getId())){
				return null;
			}
			
			Optional<UsuarioModel> usuarioOpt = repositoryUsuario.findById(postagem.getUsuario().getId());
			UsuarioModel usuario = usuarioOpt.get();
			
			tarefa.setStatus(postagem.getTarefa().getStatus());
			tarefa.setPostagem(postagem);
			
			postagem.setUsuario(usuario);
			postagem.setTarefa(tarefa);			
			return repository.save(postagem);
		}
	}

	@ApiOperation(value = "Deleta uma postagem de acordo com o Id")
	@DeleteMapping("/id")
	public ResponseEntity<Boolean> deletePostagemId(@RequestParam int id) {
		PostagemModel postagem = repository.findById(id);

		if (Objects.isNull(postagem)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			List<TarefaModel> tarefas = repositoryTarefa.findAll();
			for(TarefaModel tarefa : tarefas) {
				if(tarefa.getPostagens().contains(postagem)) {
					tarefa.setStatus(EnumStatus.Pendente);
					tarefa.getPostagens().remove(postagem);
				}
			}
			
			List<UsuarioModel> usuarios = repositoryUsuario.findAll();
			for(UsuarioModel usuario : usuarios) {
				if(usuario.getPostagens().contains(postagem)) {
					usuario.getPostagens().remove(postagem);
				}
			}
						
			repository.deleteById(id);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}
	}

	public boolean idValido(int id) {
		PostagemModel postagem = repository.findById(id);

		if (Objects.isNull(postagem))
			return false;
		else
			return true;
	}

	public boolean idTarefaValido(int id) {
		TarefaModel tarefa = repositoryTarefa.findById(id);

		if (Objects.isNull(tarefa))
			return false;
		else
			return true;
	}
	
	public boolean idUsuarioValido(int id) {
		UsuarioModel usuario = repositoryUsuario.findById(id);

		if (Objects.isNull(usuario))
			return false;
		else
			return true;
	}
}