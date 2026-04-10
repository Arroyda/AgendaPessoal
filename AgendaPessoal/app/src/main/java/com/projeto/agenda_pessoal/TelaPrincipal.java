package com.projeto.agenda_pessoal;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class TelaPrincipal extends AppCompatActivity {

  private EditText campoNome, campoCelular, campoCorreio, campoLocalidade, campoPesquisa;
  private Spinner seletorGrupo, seletorFiltro;
  private Switch toggleDestaque;
  private Button btnGravar, btnResetar, btnExibirDestaques;
  private ListView listagemPessoas;

  private GerenciadorBanco banco;
  private PessoaAdapter adaptador;
  private List<Pessoa> registros;
  private Pessoa pessoaSelecionada = null;
  private boolean exibindoDestaques = false;

  private String[] grupos = {"Família", "Amigos", "Trabalho", "Outros"};
  private String[] gruposFiltro = {"Todas", "Família", "Amigos", "Trabalho", "Outros"};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tela_principal);

    banco = new GerenciadorBanco(this);

    vincularComponentes();
    prepararSeletores();
    carregarRegistros();
    definirAcoes();
  }

  private void vincularComponentes() {
    campoNome = findViewById(R.id.campoNome);
    campoCelular = findViewById(R.id.campoCelular);
    campoCorreio = findViewById(R.id.campoCorreio);
    campoLocalidade = findViewById(R.id.campoLocalidade);
    campoPesquisa = findViewById(R.id.campoPesquisa);
    seletorGrupo = findViewById(R.id.seletorGrupo);
    seletorFiltro = findViewById(R.id.seletorFiltro);
    toggleDestaque = findViewById(R.id.toggleDestaque);
    btnGravar = findViewById(R.id.btnGravar);
    btnResetar = findViewById(R.id.btnResetar);
    btnExibirDestaques = findViewById(R.id.btnExibirDestaques);
    listagemPessoas = findViewById(R.id.listagemPessoas);
  }

  private void prepararSeletores() {
    ArrayAdapter<String> adaptadorGrupo = new ArrayAdapter<>(
        this, android.R.layout.simple_spinner_item, grupos);
    adaptadorGrupo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    seletorGrupo.setAdapter(adaptadorGrupo);

    ArrayAdapter<String> adaptadorFiltro = new ArrayAdapter<>(
        this, android.R.layout.simple_spinner_item, gruposFiltro);
    adaptadorFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    seletorFiltro.setAdapter(adaptadorFiltro);
  }

  private void carregarRegistros() {
    registros = banco.obterTodos();
    adaptador = new PessoaAdapter(this, registros);
    listagemPessoas.setAdapter(adaptador);
  }

  private void definirAcoes() {

    btnGravar.setOnClickListener(v -> {
      if (checarCampos()) {
        if (pessoaSelecionada == null) {
          gravarPessoa();
        } else {
          editarPessoa();
        }
      }
    });

    btnResetar.setOnClickListener(v -> resetarFormulario());

    listagemPessoas.setOnItemClickListener((pai, view, posicao, id) -> {
      Pessoa pessoa = registros.get(posicao);
      preencherFormulario(pessoa);
    });

    listagemPessoas.setOnItemLongClickListener((pai, view, posicao, id) -> {
      Pessoa pessoa = registros.get(posicao);
      pedirConfirmacaoRemocao(pessoa);
      return true;
    });

    seletorFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> pai, View view, int posicao, long id) {
        aplicarFiltroGrupo(gruposFiltro[posicao]);
      }
      @Override
      public void onNothingSelected(AdapterView<?> pai) {}
    });

    campoPesquisa.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int inicio, int qtd, int depois) {}
      @Override
      public void onTextChanged(CharSequence s, int inicio, int antes, int qtd) {
        String termo = s.toString().trim();
        if (termo.isEmpty()) {
          carregarRegistros();
        } else {
          List<Pessoa> encontrados = banco.obterPorNome(termo);
          adaptador.recarregarLista(encontrados);
        }
      }
      @Override
      public void afterTextChanged(Editable s) {}
    });

    btnExibirDestaques.setOnClickListener(v -> {
      exibindoDestaques = !exibindoDestaques;
      if (exibindoDestaques) {
        List<Pessoa> destaques = banco.obterDestaques();
        adaptador.recarregarLista(destaques);
        btnExibirDestaques.setText("Mostrar Todos");
      } else {
        carregarRegistros();
        btnExibirDestaques.setText("Só Destaques");
      }
    });
  }

  private boolean checarCampos() {
    String nome = campoNome.getText().toString().trim();
    String celular = campoCelular.getText().toString().trim();
    String correio = campoCorreio.getText().toString().trim();

    if (nome.isEmpty()) {
      campoNome.setError("Nome é obrigatório");
      campoNome.requestFocus();
      return false;
    }
    if (celular.isEmpty()) {
      campoCelular.setError("Celular é obrigatório");
      campoCelular.requestFocus();
      return false;
    }
    if (correio.isEmpty()) {
      campoCorreio.setError("E-mail é obrigatório");
      campoCorreio.requestFocus();
      return false;
    }
    return true;
  }

  private Pessoa extrairPessoaDoFormulario() {
    Pessoa p = new Pessoa();
    p.setNomeCompleto(campoNome.getText().toString().trim());
    p.setCelular(campoCelular.getText().toString().trim());
    p.setCorreio(campoCorreio.getText().toString().trim());
    p.setGrupo(seletorGrupo.getSelectedItem().toString());
    p.setLocalidade(campoLocalidade.getText().toString().trim());
    p.setDestaque(toggleDestaque.isChecked());
    return p;
  }

  private void gravarPessoa() {
    Pessoa novaPessoa = extrairPessoaDoFormulario();
    long codigo = banco.adicionarPessoa(novaPessoa);
    if (codigo > 0) {
      Toast.makeText(this, "Registro salvo com sucesso!", Toast.LENGTH_SHORT).show();
      resetarFormulario();
      carregarRegistros();
    } else {
      Toast.makeText(this, "Erro ao salvar registro.", Toast.LENGTH_SHORT).show();
    }
  }

  private void editarPessoa() {
    Pessoa pessoa = extrairPessoaDoFormulario();
    pessoa.setCodigo(pessoaSelecionada.getCodigo());
    int alterados = banco.modificarPessoa(pessoa);
    if (alterados > 0) {
      Toast.makeText(this, "Registro atualizado!", Toast.LENGTH_SHORT).show();
      resetarFormulario();
      carregarRegistros();
    } else {
      Toast.makeText(this, "Erro ao atualizar.", Toast.LENGTH_SHORT).show();
    }
  }

  private void preencherFormulario(Pessoa pessoa) {
    pessoaSelecionada = pessoa;
    campoNome.setText(pessoa.getNomeCompleto());
    campoCelular.setText(pessoa.getCelular());
    campoCorreio.setText(pessoa.getCorreio());
    campoLocalidade.setText(pessoa.getLocalidade());
    toggleDestaque.setChecked(pessoa.isDestaque());

    for (int i = 0; i < grupos.length; i++) {
      if (grupos[i].equals(pessoa.getGrupo())) {
        seletorGrupo.setSelection(i);
        break;
      }
    }

    btnGravar.setText("Atualizar");
    Toast.makeText(this, "Editando: " + pessoa.getNomeCompleto(), Toast.LENGTH_SHORT).show();
  }

  private void pedirConfirmacaoRemocao(Pessoa pessoa) {
    new AlertDialog.Builder(this)
        .setTitle("Remover Registro")
        .setMessage("Deseja remover " + pessoa.getNomeCompleto() + "?")
        .setPositiveButton("Remover", (dialogo, opcao) -> {
          int alterados = banco.removerPessoa(pessoa.getCodigo());
          if (alterados > 0) {
            Toast.makeText(this, "Registro removido.", Toast.LENGTH_SHORT).show();
            if (pessoaSelecionada != null &&
                pessoaSelecionada.getCodigo() == pessoa.getCodigo()) {
              resetarFormulario();
            }
            carregarRegistros();
          }
        })
        .setNegativeButton("Cancelar", null)
        .show();
  }

  private void aplicarFiltroGrupo(String grupo) {
    List<Pessoa> resultado;
    if (grupo.equals("Todas")) {
      resultado = banco.obterTodos();
    } else {
      resultado = banco.obterPorGrupo(grupo);
    }
    adaptador.recarregarLista(resultado);
  }

  private void resetarFormulario() {
    campoNome.setText("");
    campoCelular.setText("");
    campoCorreio.setText("");
    campoLocalidade.setText("");
    seletorGrupo.setSelection(0);
    toggleDestaque.setChecked(false);
    pessoaSelecionada = null;
    btnGravar.setText("Gravar");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (banco != null) {
      banco.close();
    }
  }
}
