package com.example.sdmws

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import br.edu.ifsp.scl.sdm.pa2.sdmws.viewmodel.SdmWsViewModel
import com.example.sdmws.databinding.ActivityMainBinding
import com.example.sdmws.model.model.Semestre

//import com.example.sdmws.viewmodel.SdmWsViewModel

class MainActivity : AppCompatActivity() {
    private val activityMainBinding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var sdmWsViewModel: SdmWsViewModel

    // Adapter do spinner de semestre
    private val listaSemestres = mutableListOf<Int>()
    private lateinit var semestreSpAdapter: ArrayAdapter<Int>

    // Adapter do spinner de siglas
    private val listaSiglasDisciplinas = mutableListOf<String>()
    private lateinit var siglasDisciplinasSpAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        // Instanciando viewmodel e chamando getCurso
        sdmWsViewModel = ViewModelProvider
            .AndroidViewModelFactory(this.application)
            .create(SdmWsViewModel::class.java)

        sdmWsViewModel.cursoMld.observe(this) { curso ->
            val cursoSb = StringBuilder()
            cursoSb.append("Nome: ${curso.nome}\n")
            cursoSb.append("Sigla: ${curso.sigla}\n")
            cursoSb.append("Quantidade de semestres: ${curso.semestres}\n")
            cursoSb.append("Quantidade de horas: ${curso.horas}\n")
            activityMainBinding.cursoTv.text = cursoSb.toString()

            // Preenchendo o spinner de semestre
            curso.semestres.also { semestres ->
                listaSemestres.clear()
                listaSemestres.addAll(1..semestres)
                semestreSpAdapter.notifyDataSetChanged()
            }
        }

        // Instanciando adapter do spinner semestre
        semestreSpAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listaSemestres
        )
        activityMainBinding.semestreSp.adapter = semestreSpAdapter


        // Instanciando adapter do spinner siglas
        siglasDisciplinasSpAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listaSiglasDisciplinas
        )
        activityMainBinding.siglasSp.adapter = siglasDisciplinasSpAdapter

        // Adicionando listener ao spinner de semestre para buscar um semestre
        activityMainBinding.semestreSp.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicao: Int, p3: Long) {
                val semestre = listaSemestres[posicao]
                sdmWsViewModel.getSemestre(semestre)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        // Observando o mld semestre
        sdmWsViewModel.semestreMld.observe(this) { semestre ->
            listaSiglasDisciplinas.clear()
            semestre.forEach { disciplina ->
                disciplina.sigla.also { sigla ->
                    listaSiglasDisciplinas.add(sigla)
                }
            }
            siglasDisciplinasSpAdapter.notifyDataSetChanged()
        }

        sdmWsViewModel.getCurso()
    }
}