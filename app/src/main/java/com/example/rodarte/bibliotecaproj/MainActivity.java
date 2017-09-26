package com.example.rodarte.bibliotecaproj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.rodarte.bibliotecaproj.modelo.Livro;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText editNome, editEditora, editAutor, editAno;
    ListView listV_dados;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Livro> listLivro = new ArrayList<Livro>();
    private ArrayAdapter<Livro> arrayAdapterLivro;

    Livro livroSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editNome = (EditText) findViewById(R.id.editNome);
        editEditora = (EditText) findViewById(R.id.editEditora);
        editAutor = (EditText) findViewById(R.id.editAutor);
        editAno = (EditText) findViewById(R.id.editAno);
        listV_dados = (ListView)findViewById(R.id.listV_dados);

        inicializarFirebase();
        eventoDatabase();

        listV_dados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                livroSelecionada = (Livro)parent.getItemAtPosition(position);
                editNome.setText(livroSelecionada.getNome());
                editEditora.setText(livroSelecionada.getEditora());
                editAutor.setText(livroSelecionada.getAutor());
                /*editAno.setText(livroSelecionada.getAno());*/


                /* Na Linhas 59 ( do metodo de Click para selecionar ) e na Linha 118 ( do metodo de Atualização do CRUD ) está dando erro
                   com os respectivos parametros de Integer, no banco de dados, com o Inteiro do Ano
                   não sei o motivo do porque quando seleciono no android o Item da ListVIew, meu APP
                   desliga.
                 */


            }
        });

    }

    private void eventoDatabase() {
        databaseReference.child("Livro").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listLivro.clear();
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    Livro l = objSnapshot.getValue(Livro.class);
                    listLivro.add(l);
                }
                arrayAdapterLivro = new ArrayAdapter<Livro>(MainActivity.this,
                        android.R.layout.simple_list_item_1,listLivro);
                listV_dados.setAdapter(arrayAdapterLivro);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_novo){
            Livro l = new Livro();
            l.setUid(UUID.randomUUID().toString());
            l.setNome(editNome.getText().toString());
            l.setEditora(editEditora.getText().toString());
            l.setAutor(editAutor.getText().toString());
            l.setAno(Integer.parseInt(editAno.getText().toString()));
            databaseReference.child("Livro").child(l.getUid()).setValue(l);
            limparCampos();
        }else if (id == R.id.menu_atualiza){
            Livro l = new Livro();
            l.setUid(livroSelecionada.getUid());
            l.setNome(editNome.getText().toString().trim());
            l.setEditora(editEditora.getText().toString().trim());
            l.setAutor(editAutor.getText().toString().trim());
            /*l.setAno(Integer.parseInt(editAno.getText().toString().trim()));*/
            databaseReference.child("Livro").child(l.getUid()).setValue(l);
            limparCampos();
        }else if (id == R.id.menu_deleta){
            Livro l = new Livro();
            l.setUid(livroSelecionada.getUid());
            databaseReference.child("Livro").child(l.getUid()).removeValue();
            limparCampos();
        }

        return true;
    }

    private void limparCampos() {
        editNome.setText("");
        editEditora.setText("");
        editAutor.setText("");
        editAno.setText("");
    }
}
