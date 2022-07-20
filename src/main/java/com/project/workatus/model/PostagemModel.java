package com.project.workatus.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import io.swagger.annotations.ApiModelProperty;

@Getter
@Setter
@Table(name = "tbPostagem")
@Entity(name="tbPostagem")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PostagemModel {

    public PostagemModel(String comentario, TarefaModel tarefa) {
        this.comentario = comentario;
        this.tarefa = tarefa;
    }
    
    public PostagemModel() {
    	
    }

    @ApiModelProperty(value = "Id da postagem")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="POS_ID")
    private Integer id;

    @ApiModelProperty(value = "Comentário da postagem")
    @Column(name="POS_COMENTARIO")
    private String comentario;

    @ApiModelProperty(value = "Tarefa da postagem")
    @ManyToOne
    @JoinColumn(nullable = false, name="TAR_ID")
    private TarefaModel tarefa;
    
    @ApiModelProperty(value = "Usuário da postagem")
    @ManyToOne
    @JoinColumn(nullable = false, name="USU_ID")
    private UsuarioModel usuario;

    public Integer getId() {
        return id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public TarefaModel getTarefa() {
        return tarefa;
    }

    public void setTarefa(TarefaModel tarefa) {
        this.tarefa = tarefa;
    }
    
    public UsuarioModel getUsuario() {
    	return usuario;
    }
    
    public void setUsuario(UsuarioModel usuario) {
    	this.usuario = usuario;
    }

}
