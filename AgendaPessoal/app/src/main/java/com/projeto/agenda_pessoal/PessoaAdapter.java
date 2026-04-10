package com.projeto.agenda_pessoal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PessoaAdapter extends ArrayAdapter<Pessoa> {

  private Context contexto;
  private List<Pessoa> listaPessoas;

  public PessoaAdapter(Context contexto, List<Pessoa> listaPessoas) {
    super(contexto, R.layout.item_pessoa, listaPessoas);
    this.contexto = contexto;
    this.listaPessoas = listaPessoas;
  }

  @Override
  public View getView(int posicao, View viewReciclada, ViewGroup pai) {
    if (viewReciclada == null) {
      LayoutInflater inflador = LayoutInflater.from(contexto);
      viewReciclada = inflador.inflate(R.layout.item_pessoa, pai, false);
    }

    Pessoa pessoa = listaPessoas.get(posicao);

    TextView tvNomeCompleto = viewReciclada.findViewById(R.id.tvNomeCompletoItem);
    TextView tvCelular = viewReciclada.findViewById(R.id.tvCelularItem);
    TextView tvCorreio = viewReciclada.findViewById(R.id.tvCorreioItem);
    TextView tvGrupo = viewReciclada.findViewById(R.id.tvGrupoItem);
    TextView tvLocalidade = viewReciclada.findViewById(R.id.tvLocalidadeItem);
    TextView tvDestaque = viewReciclada.findViewById(R.id.tvDestaqueItem);

    tvNomeCompleto.setText(pessoa.getNomeCompleto());
    tvCelular.setText(pessoa.getCelular());
    tvCorreio.setText(pessoa.getCorreio());
    tvGrupo.setText(pessoa.getGrupo());
    tvLocalidade.setText(pessoa.getLocalidade());
    tvDestaque.setText(pessoa.isDestaque() ? "★ Destaque" : "");

    return viewReciclada;
  }

  public void recarregarLista(List<Pessoa> novaLista) {
    listaPessoas.clear();
    listaPessoas.addAll(novaLista);
    notifyDataSetChanged();
  }
}
