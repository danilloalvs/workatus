package com.project.workatus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.workatus.model.PostagemModel;
import com.project.workatus.model.ProjetoModel;
import com.project.workatus.model.TarefaModel;
import com.project.workatus.model.UsuarioModel;
import com.project.workatus.model.enums.EnumCargo;
import com.project.workatus.model.enums.EnumStatus;
import com.project.workatus.repository.ProjetoRepository;
import com.project.workatus.repository.TarefaRepository;
import com.project.workatus.repository.UsuarioRepository;

import io.swagger.annotations.ApiOperation;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/tarefa")
public class TarefaController {

	private final TarefaRepository repository;
	private final UsuarioRepository repositoryUsuario;
	private final ProjetoRepository repositoryProjeto;
	long millis = System.currentTimeMillis();
	java.sql.Date dataAtual = new java.sql.Date(millis);

	public TarefaController(TarefaRepository repository, UsuarioRepository repositoryUsuario,
			ProjetoRepository repositoryProjeto) {
		this.repository = repository;
		this.repositoryUsuario = repositoryUsuario;
		this.repositoryProjeto = repositoryProjeto;
	}

	@ApiOperation(value = "Retorna todas as tarefas cadastradas")
	@GetMapping
	public List<TarefaModel> getAll() {
		return repository.findAll();
	}

	@ApiOperation(value = "Retorna uma tarefa de acordo com o Id")
	@GetMapping("/id")
	public TarefaModel getTarefaId(@RequestParam int id) {
		return repository.findById(id);
	}

	@ApiOperation(value = "Retorna uma tarefa de acordo com o Titulo")
	@GetMapping("/titulo")
	public TarefaModel getTarefaTitulo(@RequestParam String titulo) {
		return repository.findByTitulo(titulo);
	}

	@ApiOperation(value = "Retorna a lista de postagens desta Tarefa")
	@GetMapping("/postagens")
	public List<PostagemModel> getPostagensId(@RequestParam int id) {
		return repository.findById(id).getPostagens();
	}

	@ApiOperation(value = "Insere uma tarefa no sistema")
	@PostMapping
	public TarefaModel insertTarefa(@RequestBody TarefaModel tarefa) {		
		if(!validaCamposNulos(tarefa)) {
			return null;
		}			

		boolean tituloExiste = tituloValido(tarefa.getTitulo());
		boolean idUsuarioAdministradorExiste = idUsuarioValido(tarefa.getUsuarioAdministrador().getId());
		boolean idUsuarioFuncionarioExiste = idUsuarioValido(tarefa.getUsuarioFuncionario().getId());
		boolean idProjetoExiste = idProjetoValido(tarefa.getProjeto().getId());

		if (!validaCamposNulos(tarefa) || tituloExiste || !idUsuarioAdministradorExiste || !idUsuarioFuncionarioExiste || !idProjetoExiste
				|| tarefa.getDataFinal().before(tarefa.getDataInicio())) {
			return null;
		} else {
			TarefaModel cadastroTarefa = new TarefaModel();
			
			Optional<UsuarioModel> usuarioAdministrador = repositoryUsuario
					.findById(tarefa.getUsuarioAdministrador().getId());
			UsuarioModel usuarioAdm = usuarioAdministrador.get();

			if (!usuarioAdm.getCargo().equals(EnumCargo.Administrador))
				return null;
			else
				cadastroTarefa.setUsuarioAdministrador(usuarioAdm);

			Optional<UsuarioModel> usuarioFuncionario = repositoryUsuario
					.findById(tarefa.getUsuarioFuncionario().getId());
			UsuarioModel usuarioFunc = usuarioFuncionario.get();
			cadastroTarefa.setUsuarioFuncionario(usuarioFunc);

			Optional<ProjetoModel> projetoOpt = repositoryProjeto.findById(tarefa.getProjeto().getId());
			ProjetoModel projeto = projetoOpt.get();
			cadastroTarefa.setProjeto(projeto);

			cadastroTarefa.setDataCadastro(dataAtual);
			cadastroTarefa.setDataFinal(formataData(tarefa.getDataFinal()));
			cadastroTarefa.setDataInicio(formataData(tarefa.getDataInicio()));
			cadastroTarefa.setDescricao(tarefa.getDescricao());
			if (Objects.isNull(tarefa.getStatus()))
				cadastroTarefa.setStatus(EnumStatus.Pendente);
			else
				cadastroTarefa.setStatus(tarefa.getStatus());

			cadastroTarefa.setTitulo(tarefa.getTitulo());
			projeto.setTarefas(cadastroTarefa);
			projeto.setFuncionarios(usuarioFunc);

			usuarioFunc.setTarefasAtribuidas(cadastroTarefa);
			usuarioFunc.setProjetos(projeto);
			usuarioAdm.setTarefasCadastradas(cadastroTarefa);
			return repository.save(cadastroTarefa);
		}
	}

	@ApiOperation(value = "Deleta uma tarefa de acordo com o Id")
	@DeleteMapping("/id")
	public ResponseEntity<Boolean> deleteTarefaId(@RequestParam int id) {
		TarefaModel tarefa = repository.findById(id);

		if (Objects.isNull(tarefa)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			List<ProjetoModel> projetos = repositoryProjeto.findAll();
			for (ProjetoModel projeto : projetos) {
				if (projeto.getTarefas().contains(tarefa)) {
					projeto.getTarefas().remove(tarefa);
				}
				if (projeto.getFuncionarios().contains(tarefa.getUsuarioFuncionario())) {
					projeto.getFuncionarios().remove(tarefa.getUsuarioFuncionario());
				}
			}

			List<UsuarioModel> usuariosFunc = repositoryUsuario.findAll();
			for (UsuarioModel usuarioFunc : usuariosFunc) {
				if (usuarioFunc.getTarefasAtribuidas().contains(tarefa)) {
					usuarioFunc.getTarefasAtribuidas().remove(tarefa);
				}
				if (usuarioFunc.getProjetos().contains(tarefa.getProjeto())) {
					usuarioFunc.getProjetos().remove(tarefa.getProjeto());
				}
			}

			List<UsuarioModel> usuariosAdm = repositoryUsuario.findAll();
			for (UsuarioModel usuarioAdm : usuariosAdm) {
				if (usuarioAdm.getTarefasCadastradas().contains(tarefa)) {
					usuarioAdm.getTarefasCadastradas().remove(tarefa);
				} else if (usuarioAdm.getTarefasAtribuidas().contains(tarefa)) {
					usuarioAdm.getTarefasAtribuidas().remove(tarefa);
				}
			}

			repository.deleteById(id);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}
	}

	@ApiOperation(value = "Deleta uma tarefa de acordo com o Titulo")
	@DeleteMapping("/titulo")
	public ResponseEntity<Boolean> deleteTarefaTitulo(@RequestParam String titulo) {
		TarefaModel tarefa = repository.findByTitulo(titulo);

		if (Objects.isNull(tarefa)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			List<ProjetoModel> projetos = repositoryProjeto.findAll();
			for (ProjetoModel projeto : projetos) {
				if (projeto.getTarefas().contains(tarefa)) {
					projeto.getTarefas().remove(tarefa);
				}
				if (projeto.getFuncionarios().contains(tarefa.getUsuarioFuncionario())) {
					projeto.getFuncionarios().remove(tarefa.getUsuarioFuncionario());
				}
			}

			List<UsuarioModel> usuariosFunc = repositoryUsuario.findAll();
			for (UsuarioModel usuarioFunc : usuariosFunc) {
				if (usuarioFunc.getTarefasAtribuidas().contains(tarefa)) {
					usuarioFunc.getTarefasAtribuidas().remove(tarefa);
				}
				if (usuarioFunc.getProjetos().contains(tarefa.getProjeto())) {
					usuarioFunc.getProjetos().remove(tarefa.getProjeto());
				}
			}

			List<UsuarioModel> usuariosAdm = repositoryUsuario.findAll();
			for (UsuarioModel usuarioAdm : usuariosAdm) {
				if (usuarioAdm.getTarefasCadastradas().contains(tarefa)) {
					usuarioAdm.getTarefasCadastradas().remove(tarefa);
				} else if (usuarioAdm.getTarefasAtribuidas().contains(tarefa)) {
					usuarioAdm.getTarefasAtribuidas().remove(tarefa);
				}
			}
			repository.deleteById(tarefa.getId());
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}

	}

	@ApiOperation(value = "Atualiza as propriedades de uma tarefa do sistema")
	@PutMapping
	public TarefaModel putTarefa(@RequestBody TarefaModel tarefa) {
		if (Objects.isNull(tarefa.getId()) || !validaCamposNulos(tarefa)) {
			return null;
		} 

		boolean idExiste = idTarefaValido(tarefa.getId());
		boolean idUsuarioAdministradorExiste = idUsuarioValido(tarefa.getUsuarioAdministrador().getId());
		boolean idUsuarioFuncionarioExiste = idUsuarioValido(tarefa.getUsuarioFuncionario().getId());
		boolean idProjetoExiste = idProjetoValido(tarefa.getProjeto().getId());

		if (!idExiste || !idUsuarioAdministradorExiste || !idUsuarioFuncionarioExiste || !idProjetoExiste) {
			return null;
		} else {
			TarefaModel tarefaComMesmoTitulo = repository.findByTitulo(tarefa.getTitulo());

			if (Objects.isNull(tarefaComMesmoTitulo) || tarefaComMesmoTitulo.getId() == tarefa.getId()) {
				TarefaModel tarefaCadastrada = repository.findById(tarefa.getId()).get();

				if (tarefa.getDataFinal().before(tarefa.getDataInicio())) {
					return null;
				} else {
					Optional<UsuarioModel> usuarioAdministrador = repositoryUsuario
							.findById(tarefa.getUsuarioAdministrador().getId());
					UsuarioModel usuarioAdm = usuarioAdministrador.get();

					if (!usuarioAdm.getCargo().equals(EnumCargo.Administrador))
						return null;
					else
						tarefaCadastrada.setUsuarioAdministrador(usuarioAdm);

					Optional<UsuarioModel> usuarioFuncionario = repositoryUsuario
							.findById(tarefa.getUsuarioFuncionario().getId());
					UsuarioModel usuarioFunc = usuarioFuncionario.get();
					tarefaCadastrada.setUsuarioFuncionario(usuarioFunc);

					Optional<ProjetoModel> projetoOpt = repositoryProjeto.findById(tarefa.getProjeto().getId());
					ProjetoModel projeto = projetoOpt.get();
					tarefaCadastrada.setProjeto(projeto);

					projeto.setFuncionarios(usuarioFunc);
					usuarioFunc.setProjetos(projeto);

					tarefaCadastrada.setDataCadastro(dataAtual);
					tarefaCadastrada.setDataFinal(formataData(tarefa.getDataFinal()));
					tarefaCadastrada.setDataInicio(formataData(tarefa.getDataInicio()));
					tarefaCadastrada.setDescricao(tarefa.getDescricao());
					if (!Objects.isNull(tarefa.getStatus()))
						tarefaCadastrada.setStatus(tarefa.getStatus());

					tarefaCadastrada.setTitulo(tarefa.getTitulo());

					if (!projeto.getTarefas().contains(tarefaCadastrada))
						projeto.setTarefas(tarefaCadastrada);

					if (!usuarioFunc.getTarefasAtribuidas().contains(tarefaCadastrada))
						usuarioFunc.setTarefasAtribuidas(tarefaCadastrada);

					if (!usuarioAdm.getTarefasCadastradas().contains(tarefaCadastrada))
						usuarioAdm.setTarefasCadastradas(tarefaCadastrada);

					return repository.save(tarefaCadastrada);
				}
			} else
				return null;
		}

	}
	
	public boolean validaCamposNulos(TarefaModel tarefa) {
		if (Objects.isNull(tarefa.getTitulo()) || Objects.isNull(tarefa.getUsuarioAdministrador().getId())
				|| Objects.isNull(tarefa.getUsuarioFuncionario().getId()) || Objects.isNull(tarefa.getProjeto().getId())
				|| Objects.isNull(tarefa.getDataFinal()) || Objects.isNull(tarefa.getDataInicio())) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean idTarefaValido(int id) {
		TarefaModel tarefa = repository.findById(id);

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

	public boolean idProjetoValido(int id) {
		ProjetoModel projeto = repositoryProjeto.findById(id);

		if (Objects.isNull(projeto))
			return false;
		else
			return true;
	}

	public boolean tituloValido(String titulo) {
		TarefaModel tarefa = repository.findByTitulo(titulo);

		if (Objects.isNull(tarefa))
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
