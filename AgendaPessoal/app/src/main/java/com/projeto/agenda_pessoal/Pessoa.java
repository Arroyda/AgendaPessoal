package com.projeto.agenda_pessoal;

public class Pessoa {
  private int codigo;
  private String nomeCompleto;
  private String celular;
  private String correio;
  private String grupo;
  private String localidade;
  private boolean destaque;

  public Pessoa() {}

  public Pessoa(int codigo, String nomeCompleto, String celular, String correio,
          String grupo, String localidade, boolean destaque) {
    this.codigo = codigo;
    this.nomeCompleto = nomeCompleto;
    this.celular = celular;
    this.correio = correio;
    this.grupo = grupo;
    this.localidade = localidade;
    this.destaque = destaque;
  }

  public int getCodigo() { return codigo; }
  public void setCodigo(int codigo) { this.codigo = codigo; }

  public String getNomeCompleto() { return nomeCompleto; }
  public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

  public String getCelular() { return celular; }
  public void setCelular(String celular) { this.celular = celular; }

  public String getCorreio() { return correio; }
  public void setCorreio(String correio) { this.correio = correio; }

  public String getGrupo() { return grupo; }
  public void setGrupo(String grupo) { this.grupo = grupo; }

  public String getLocalidade() { return localidade; }
  public void setLocalidade(String localidade) { this.localidade = localidade; }

  public boolean isDestaque() { return destaque; }
  public void setDestaque(boolean destaque) { this.destaque = destaque; }
}
