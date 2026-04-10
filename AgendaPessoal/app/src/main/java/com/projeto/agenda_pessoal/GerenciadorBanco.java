package com.projeto.agenda_pessoal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class GerenciadorBanco extends SQLiteOpenHelper {

  private static final String NOME_BANCO = "agenda.db";
  private static final int VERSAO_BANCO = 1;

  public static final String TABELA_PESSOAS = "pessoas";
  public static final String COL_CODIGO = "codigo";
  public static final String COL_NOME_COMPLETO = "nome_completo";
  public static final String COL_CELULAR = "celular";
  public static final String COL_CORREIO = "correio";
  public static final String COL_GRUPO = "grupo";
  public static final String COL_LOCALIDADE = "localidade";
  public static final String COL_DESTAQUE = "destaque";

  private static final String SQL_MONTAR_TABELA =
      "CREATE TABLE " + TABELA_PESSOAS + " (" +
          COL_CODIGO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
          COL_NOME_COMPLETO + " TEXT NOT NULL, " +
          COL_CELULAR + " TEXT NOT NULL, " +
          COL_CORREIO + " TEXT NOT NULL, " +
          COL_GRUPO + " TEXT, " +
          COL_LOCALIDADE + " TEXT, " +
          COL_DESTAQUE + " INTEGER DEFAULT 0" +
          ")";

  public GerenciadorBanco(Context contexto) {
    super(contexto, NOME_BANCO, null, VERSAO_BANCO);
  }

  @Override
  public void onCreate(SQLiteDatabase banco) {
    banco.execSQL(SQL_MONTAR_TABELA);
  }

  @Override
  public void onUpgrade(SQLiteDatabase banco, int versaoAnterior, int versaoAtual) {
    banco.execSQL("DROP TABLE IF EXISTS " + TABELA_PESSOAS);
    onCreate(banco);
  }

  public long adicionarPessoa(Pessoa pessoa) {
    SQLiteDatabase banco = this.getWritableDatabase();
    ContentValues dados = new ContentValues();
    dados.put(COL_NOME_COMPLETO, pessoa.getNomeCompleto());
    dados.put(COL_CELULAR, pessoa.getCelular());
    dados.put(COL_CORREIO, pessoa.getCorreio());
    dados.put(COL_GRUPO, pessoa.getGrupo());
    dados.put(COL_LOCALIDADE, pessoa.getLocalidade());
    dados.put(COL_DESTAQUE, pessoa.isDestaque() ? 1 : 0);

    long codigo = banco.insert(TABELA_PESSOAS, null, dados);
    banco.close();
    return codigo;
  }

  public List<Pessoa> obterTodos() {
    List<Pessoa> registros = new ArrayList<>();
    SQLiteDatabase banco = this.getReadableDatabase();
    Cursor ponteiro = banco.query(TABELA_PESSOAS, null, null, null,
        null, null, COL_NOME_COMPLETO + " ASC");

    if (ponteiro.moveToFirst()) {
      do {
        registros.add(ponteiroParaPessoa(ponteiro));
      } while (ponteiro.moveToNext());
    }
    ponteiro.close();
    banco.close();
    return registros;
  }

  public List<Pessoa> obterPorGrupo(String grupo) {
    List<Pessoa> registros = new ArrayList<>();
    SQLiteDatabase banco = this.getReadableDatabase();
    Cursor ponteiro = banco.query(TABELA_PESSOAS, null,
        COL_GRUPO + " = ?",
        new String[]{grupo},
        null, null, COL_NOME_COMPLETO + " ASC");

    if (ponteiro.moveToFirst()) {
      do {
        registros.add(ponteiroParaPessoa(ponteiro));
      } while (ponteiro.moveToNext());
    }
    ponteiro.close();
    banco.close();
    return registros;
  }

  public List<Pessoa> obterDestaques() {
    List<Pessoa> registros = new ArrayList<>();
    SQLiteDatabase banco = this.getReadableDatabase();
    Cursor ponteiro = banco.query(TABELA_PESSOAS, null,
        COL_DESTAQUE + " = 1",
        null, null, null, COL_NOME_COMPLETO + " ASC");

    if (ponteiro.moveToFirst()) {
      do {
        registros.add(ponteiroParaPessoa(ponteiro));
      } while (ponteiro.moveToNext());
    }
    ponteiro.close();
    banco.close();
    return registros;
  }

  public List<Pessoa> obterPorNome(String nome) {
    List<Pessoa> registros = new ArrayList<>();
    SQLiteDatabase banco = this.getReadableDatabase();
    Cursor ponteiro = banco.query(TABELA_PESSOAS, null,
        COL_NOME_COMPLETO + " LIKE ?",
        new String[]{"%" + nome + "%"},
        null, null, COL_NOME_COMPLETO + " ASC");

    if (ponteiro.moveToFirst()) {
      do {
        registros.add(ponteiroParaPessoa(ponteiro));
      } while (ponteiro.moveToNext());
    }
    ponteiro.close();
    banco.close();
    return registros;
  }

  public int modificarPessoa(Pessoa pessoa) {
    SQLiteDatabase banco = this.getWritableDatabase();
    ContentValues dados = new ContentValues();
    dados.put(COL_NOME_COMPLETO, pessoa.getNomeCompleto());
    dados.put(COL_CELULAR, pessoa.getCelular());
    dados.put(COL_CORREIO, pessoa.getCorreio());
    dados.put(COL_GRUPO, pessoa.getGrupo());
    dados.put(COL_LOCALIDADE, pessoa.getLocalidade());
    dados.put(COL_DESTAQUE, pessoa.isDestaque() ? 1 : 0);

    int registrosAlterados = banco.update(TABELA_PESSOAS, dados,
        COL_CODIGO + " = ?",
        new String[]{String.valueOf(pessoa.getCodigo())});
    banco.close();
    return registrosAlterados;
  }

  public int removerPessoa(int codigo) {
    SQLiteDatabase banco = this.getWritableDatabase();
    int registrosAlterados = banco.delete(TABELA_PESSOAS,
        COL_CODIGO + " = ?",
        new String[]{String.valueOf(codigo)});
    banco.close();
    return registrosAlterados;
  }

  private Pessoa ponteiroParaPessoa(Cursor ponteiro) {
    Pessoa p = new Pessoa();
    p.setCodigo(ponteiro.getInt(ponteiro.getColumnIndexOrThrow(COL_CODIGO)));
    p.setNomeCompleto(ponteiro.getString(ponteiro.getColumnIndexOrThrow(COL_NOME_COMPLETO)));
    p.setCelular(ponteiro.getString(ponteiro.getColumnIndexOrThrow(COL_CELULAR)));
    p.setCorreio(ponteiro.getString(ponteiro.getColumnIndexOrThrow(COL_CORREIO)));
    p.setGrupo(ponteiro.getString(ponteiro.getColumnIndexOrThrow(COL_GRUPO)));
    p.setLocalidade(ponteiro.getString(ponteiro.getColumnIndexOrThrow(COL_LOCALIDADE)));
    p.setDestaque(ponteiro.getInt(ponteiro.getColumnIndexOrThrow(COL_DESTAQUE)) == 1);
    return p;
  }
}
