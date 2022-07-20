package com.project.workatus.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.project.workatus.model.enums.EnumCargo;

import io.swagger.annotations.ApiModelProperty;

@Getter
@Setter
@Table(name = "tbUsuario")
@Entity(name = "tbUsuario")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UsuarioModel {

	public UsuarioModel(String login, String senha, EnumCargo cargo) {
		super();
		this.login = login;
		this.senha = senha;
		this.cargo = cargo;
		this.tarefasCadastradas = new ArrayList<TarefaModel>();
		this.tarefasAtribuidas = new ArrayList<TarefaModel>();
		this.projetos = new ArrayList<ProjetoModel>();
		this.postagens = new ArrayList<PostagemModel>();
	}

	public UsuarioModel() {
		this.tarefasCadastradas = new ArrayList<TarefaModel>();
		this.tarefasAtribuidas = new ArrayList<TarefaModel>();
		this.projetos = new ArrayList<ProjetoModel>();
		this.postagens = new ArrayList<PostagemModel>();
	}

	@ApiModelProperty(value = "Id do usuário")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USU_ID")
	private Integer id;

	@ApiModelProperty(value = "Login do usuário")
	@Column(unique = true, nullable = false, name = "USU_LOGIN")
	private String login;

	@ApiModelProperty(value = "Senha do usuário")
	@Column(nullable = false, name = "USU_SENHA")
	private String senha;

	@ApiModelProperty(value = "Cargo do usuário")
	@Column(nullable = false, name = "USU_CARGO")
	private EnumCargo cargo;

	@JsonIgnore
	@ApiModelProperty(value = "Lista de tarefas que este usuário cadastrou")
	@OneToMany(mappedBy = "usuarioAdministrador", fetch = FetchType.EAGER)
	private List<TarefaModel> tarefasCadastradas;

	@JsonIgnore
	@ApiModelProperty(value = "Lista de tarefas atribuídas a este usuário")
	@OneToMany(mappedBy = "usuarioFuncionario", fetch = FetchType.LAZY)
	private List<TarefaModel> tarefasAtribuidas;

	@JsonIgnore
	@ApiModelProperty(value = "Lista de postagens que este usuário fez")
	@OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
	private List<PostagemModel> postagens;

	@JsonIgnore
	@ApiModelProperty(value = "Lista de projetos que este usuário participa")
	@JsonInclude()
	@Transient
	private List<ProjetoModel> projetos;

	public Integer getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public EnumCargo getCargo() {
		return cargo;
	}

	public void setCargo(EnumCargo cargo) {
		this.cargo = cargo;
	}

	public void setTarefasCadastradas(TarefaModel tarefa) {
		this.tarefasCadastradas.add(tarefa);
	}

	public List<TarefaModel> getTarefasCadastradas() {
		return tarefasCadastradas;
	}

	public void setTarefasAtribuidas(TarefaModel tarefa) {
		this.tarefasAtribuidas.add(tarefa);
	}

	public List<TarefaModel> getTarefasAtribuidas() {
		return tarefasAtribuidas;
	}

	public void setPostagens(PostagemModel postagem) {
		this.postagens.add(postagem);
	}

	public List<PostagemModel> getPostagens() {
		return postagens;
	}

	public void setProjetos(ProjetoModel projeto) {
		if (!this.projetos.contains(projeto)) {
			this.projetos.add(projeto);
		}	
	}

	public List<ProjetoModel> getProjetos() {
		return projetos;
	}

	public void removeProjetos(ProjetoModel projeto) {
		this.projetos.remove(projeto);
	}

}