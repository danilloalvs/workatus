package com.project.workatus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.project.workatus.model.ProjetoModel;
import com.project.workatus.model.TarefaModel;
import com.project.workatus.model.UsuarioModel;
import com.project.workatus.repository.TarefaRepository;
import com.project.workatus.repository.UsuarioRepository;

import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

	private final UsuarioRepository repository;
	private final TarefaRepository repositoryTarefa;
	private final PasswordEncoder encoder;

	public UsuarioController(UsuarioRepository repository, PasswordEncoder encoder, TarefaRepository repositoryTarefa) {
		this.repository = repository;
		this.repositoryTarefa = repositoryTarefa;
		this.encoder = encoder;
	}

	@ApiOperation(value = "Retorna todos os usuários cadastrados")
	@GetMapping
	public List<UsuarioModel> getAll() {
		return repository.findAll();
	}

	@ApiOperation(value = "Retorna um usuário de acordo com o Id")
	@GetMapping("/id")
	public UsuarioModel getUsuarioId(@RequestParam int id) {
		return repository.findById(id);
	}

	@ApiOperation(value = "Retorna um usuário de acordo com o Login")
	@GetMapping("/login")
	public UsuarioModel getUsuarioLogin(@RequestParam String login) {
		return repository.findByLogin(login);
	}

	@ApiOperation(value = "Retorna a lista de projetos que este usuário está incluso")
	@GetMapping("/projetos")
	public List<ProjetoModel> getProjetos(@RequestParam int id) {
		List<TarefaModel> listaTarefas = repositoryTarefa.findAll();
		List<ProjetoModel> projetosUsuario = new ArrayList<ProjetoModel>();
		
		for(TarefaModel tarefa : listaTarefas) {
			if(tarefa.getUsuarioFuncionario().getId().equals(id) && !projetosUsuario.contains(tarefa.getProjeto()))
			projetosUsuario.add(tarefa.getProjeto());
		}	
		return projetosUsuario;		
	}

	@ApiOperation(value = "Retorna a lista de tarefas cadastradas por este usuário")
	@GetMapping("/tarefasCadastradas")
	public List<TarefaModel> getTarefasCadastradas(@RequestParam int id) {
		return repository.findById(id).getTarefasCadastradas();
	}

	@ApiOperation(value = "Retorna a lista de tarefas atribuídas para este usuário")
	@GetMapping("/tarefasAtribuidas")
	public List<TarefaModel> getTarefasAtribuidas(@RequestParam int id) {
		return repository.findById(id).getTarefasAtribuidas();
	}

	@ApiOperation(value = "Insere um usuário no sistema")
	@PostMapping
	public UsuarioModel insertUsuario(@RequestBody UsuarioModel usuario) {
		if(Objects.isNull(usuario.getLogin()) || Objects.isNull(usuario.getSenha()) || Objects.isNull(usuario.getCargo())) {
			return null;
		}
		
		boolean loginExiste = loginValido(usuario.getLogin());

		if (loginExiste) {
			return null;
		} else {
			return repository
					.save(new UsuarioModel(usuario.getLogin(), encoder.encode(usuario.getSenha()), usuario.getCargo()));
		}
	}

	@ApiOperation(value = "Valida se usuário e senha existem no sistema")
	@PostMapping("/check")
	public ResponseEntity<Boolean> checkUsuario(@RequestBody UsuarioModel usuario) {
		if(Objects.isNull(usuario.getLogin()) || Objects.isNull(usuario.getSenha())){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}
		
		boolean loginExiste = loginValido(usuario.getLogin());

		if (!loginExiste)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);

		UsuarioModel usuarioExiste = repository.findByLogin(usuario.getLogin());

		boolean valid = encoder.matches(usuario.getSenha(), usuarioExiste.getSenha());

		HttpStatus status = (valid) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status).body(valid);
	}

	@ApiOperation(value = "Deleta um usuário de acordo com o Id")
	@DeleteMapping("/id")
	public ResponseEntity<Boolean> deleteUsuarioId(@RequestParam int id) {
		UsuarioModel usuario = repository.findById(id);

		if (Objects.isNull(usuario)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			repository.deleteById(id);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}
	}

	@ApiOperation(value = "Deleta um usuário de acordo com o Login")
	@DeleteMapping("/login")
	public ResponseEntity<Boolean> deleteUsuarioLogin(@RequestParam String login) {
		UsuarioModel usuario = repository.findByLogin(login);

		if (Objects.isNull(usuario)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			repository.deleteById(usuario.getId());
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}

	}

	@ApiOperation(value = "Atualiza as propriedades de um usuário do sistema")
	@PutMapping
	public UsuarioModel putUsuario(@RequestBody UsuarioModel usuario) {
		boolean idExiste = idValido(usuario.getId());

		if (!idExiste) {
			return null;
		} else {
			UsuarioModel usuarioComMesmoLogin = repository.findByLogin(usuario.getLogin());

			if (Objects.isNull(usuarioComMesmoLogin) || usuarioComMesmoLogin.getId() == usuario.getId()) {
				UsuarioModel usuarioCadastrado = repository.findById(usuario.getId()).get();

				usuarioCadastrado.setLogin(usuario.getLogin());
				usuarioCadastrado.setSenha(encoder.encode(usuario.getSenha()));
				usuarioCadastrado.setCargo(usuario.getCargo());

				return repository.save(usuarioCadastrado);
			} else {
				return null;
			}
		}
	}

	public boolean idValido(int id) {
		UsuarioModel usuario = repository.findById(id);

		if (Objects.isNull(usuario))
			return false;
		else
			return true;
	}

	public boolean loginValido(String login) {
		UsuarioModel usuario = repository.findByLogin(login);

		if (Objects.isNull(usuario))
			return false;
		else
			return true;
	}

	public boolean senhaValida(int id, String senha) {
		UsuarioModel usuarioExistente = repository.findById(id);

		return encoder.matches(senha, usuarioExistente.getSenha());
	}
}