package com.project.workatus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.workatus.model.ProjetoModel;
import com.project.workatus.model.TarefaModel;
import com.project.workatus.model.UsuarioModel;
import com.project.workatus.repository.ProjetoRepository;
import com.project.workatus.repository.TarefaRepository;
import com.project.workatus.repository.UsuarioRepository;

import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/projeto")
public class ProjetoController {

	private final ProjetoRepository repository;
	private final UsuarioRepository repositoryUsuario;
	private final TarefaRepository repositoryTarefa;

	public ProjetoController(ProjetoRepository repository, UsuarioRepository repositoryUsuario, TarefaRepository repositoryTarefa) {
		this.repository = repository;
		this.repositoryUsuario = repositoryUsuario;
		this.repositoryTarefa = repositoryTarefa;
	}

	@ApiOperation(value = "Retorna todos os projetos cadastrados")
	@GetMapping
	public List<ProjetoModel> getAll() {
		return repository.findAll();
	}

	@ApiOperation(value = "Retorna um projeto de acordo com o Id")
	@GetMapping("/id")
	public ProjetoModel getProjetoId(@RequestParam int id) {
		return repository.findById(id);
	}

	@ApiOperation(value = "Retorna um projeto de acordo com o Nome")
	@GetMapping("/nome")
	public ProjetoModel getProjetoNome(@RequestParam String nome) {
		return repository.findByNome(nome);
	}

	@ApiOperation(value = "Retorna a lista de tarefas deste Projeto")
	@GetMapping("/tarefas")
	public List<TarefaModel> getTarefaId(@RequestParam int id) {
		return repository.findById(id).getTarefas();
	}

	@ApiOperation(value = "Retorna a lista de funcionarios deste Projeto")
	@GetMapping("/funcionarios")
	public List<UsuarioModel> getFuncionarioId(@RequestParam int id) {
		List<TarefaModel> listaTarefas = repositoryTarefa.findAll();
		List<UsuarioModel> usuariosProjeto = new ArrayList<UsuarioModel>();
		
		for(TarefaModel tarefa : listaTarefas) {
			if(tarefa.getProjeto().getId().equals(id) && !usuariosProjeto.contains(tarefa.getUsuarioFuncionario()))
			usuariosProjeto.add(tarefa.getUsuarioFuncionario());
		}	
		return usuariosProjeto;
	}

	@ApiOperation(value = "Insere um projeto no sistema")
	@PostMapping
	public ProjetoModel insertProjeto(@RequestBody ProjetoModel projeto) {
		boolean nomeExiste = nomeValido(projeto.getNome());

		if (nomeExiste || Objects.isNull(projeto.getDataFinal()) || Objects.isNull(projeto.getDataInicio())
				|| projeto.getDataFinal().before(projeto.getDataInicio())) {
			return null;
		} else {
			return repository.save(new ProjetoModel(projeto.getNome(), projeto.getDescricao(), formataData(projeto.getDataInicio()),
					formataData(projeto.getDataFinal())));
		}
	}

	@ApiOperation(value = "Deleta um projeto de acordo com o Id")
	@DeleteMapping("/id")
	public ResponseEntity<Boolean> deleteProjetoId(@RequestParam int id) {
		ProjetoModel projeto = repository.findById(id);

		if (Objects.isNull(projeto)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			List<UsuarioModel> usuarios = repositoryUsuario.findAll();
			for(UsuarioModel usuario : usuarios) {
				if(usuario.getProjetos().contains(projeto)) {
					usuario.getProjetos().remove(projeto);
				}
			}			
			repository.deleteById(id);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}
	}

	@ApiOperation(value = "Deleta um projeto de acordo com o Nome")
	@DeleteMapping("/nome")
	public ResponseEntity<Boolean> deleteProjetoNome(@RequestParam String nome) {
		ProjetoModel projeto = repository.findByNome(nome);

		if (Objects.isNull(projeto)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			List<UsuarioModel> usuarios = repositoryUsuario.findAll();
			for(UsuarioModel usuario : usuarios) {
				if(usuario.getProjetos().contains(projeto)) {
					usuario.getProjetos().remove(projeto);
				}
			}			
			repository.deleteById(projeto.getId());
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}

	}

	@ApiOperation(value = "Atualiza as propriedades de um projeto do sistema")
	@PutMapping
	public ProjetoModel putProjeto(@RequestBody ProjetoModel projeto) {
		boolean idExiste = idValido(projeto.getId());

		if (!idExiste || Objects.isNull(projeto.getDataFinal()) || Objects.isNull(projeto.getDataInicio())
				|| projeto.getDataFinal().before(projeto.getDataInicio())) {
			return null;
		} else {
			ProjetoModel projetoComMesmoNome = repository.findByNome(projeto.getNome());

			if (Objects.isNull(projetoComMesmoNome) || projetoComMesmoNome.getId() == projeto.getId()) {
				ProjetoModel projetoCadastrado = repository.findById(projeto.getId()).get();

				projetoCadastrado.setNome(projeto.getNome());
				projetoCadastrado.setDescricao(projeto.getDescricao());
				projetoCadastrado.setDataInicio(formataData(projeto.getDataInicio()));
				projetoCadastrado.setDataFinal(formataData(projeto.getDataFinal()));
				return repository.save(projetoCadastrado);
			} else {
				return null;
			}
		}

	}

	public boolean idValido(int id) {
		ProjetoModel projeto = repository.findById(id);

		if (Objects.isNull(projeto))
			return false;
		else
			return true;
	}

	public boolean nomeValido(String nome) {
		ProjetoModel projeto = repository.findByNome(nome);

		if (Objects.isNull(projeto))
			return false;
		else
			return true;
	}

	public java.sql.Date formataData(java.sql.Date data) {
		LocalDate dataLocal = data.toLocalDate();
		LocalDate dataCorreta = dataLocal.plusDays(1);
		Date dataRetorno = Date.from(dataCorreta.atStartOfDay(ZoneId.systemDefault()).toInstant());
		java.sql.Date dataSQL = new java.sql.Date(dataRetorno.getTime());
		return dataSQL;
	}

}