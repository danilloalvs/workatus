package com.project.workatus.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import io.swagger.annotations.ApiModelProperty;

@Getter
@Setter
@Table(name = "tbProjeto")
@Entity(name = "tbProjeto")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ProjetoModel {

	public ProjetoModel(String nome, String descricao, Date dataInicio, Date dataFinal) {
		super();
		this.nome = nome;
		this.descricao = descricao;
		this.dataInicio = dataInicio;
		this.dataFinal = dataFinal;
		this.tarefas = new ArrayList<TarefaModel>();
		this.funcionarios = new ArrayList<UsuarioModel>();
	}

	public ProjetoModel() {
		this.tarefas = new ArrayList<TarefaModel>();
		this.funcionarios = new ArrayList<UsuarioModel>();
	}

	@ApiModelProperty(value = "Id do projeto")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRO_ID")
	private Integer id;

	@ApiModelProperty(value = "Nome do projeto")
	@Column(unique = true, nullable = false, name = "PRO_NOME")
	private String nome;

	@ApiModelProperty(value = "Descrição do projeto")
	@Column(name = "PRO_DESCRICAO")
	private String descricao;

	@ApiModelProperty(value = "Data de início do projeto")
	@Column(nullable = false, name = "PRO_DATA_INICIO")
	private Date dataInicio;

	@ApiModelProperty(value = "Data final do projeto")
	@Column(nullable = false, name = "PRO_DATA_FINAL")
	private Date dataFinal;

	@JsonIgnore
	@ApiModelProperty(value = "Lista de tarefas do projeto")
	@OneToMany(mappedBy = "projeto", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TarefaModel> tarefas;

	@JsonIgnore
	@ApiModelProperty(value = "Lista de usuários neste projeto")
	@JsonInclude()
	@Transient
	private List<UsuarioModel> funcionarios;

	public Integer getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public List<TarefaModel> getTarefas() {
		return tarefas;
	}

	public void setTarefas(TarefaModel tarefa) {
		this.tarefas.add(tarefa);
	}

	public List<UsuarioModel> getFuncionarios() {
		return funcionarios;
	}

	public void setFuncionarios(UsuarioModel usuario) {
		if (!this.funcionarios.contains(usuario)) {
			this.funcionarios.add(usuario);
		}
	}

	public void removeFuncionarios(UsuarioModel usuario) {
		this.funcionarios.remove(usuario);
	}

}