package br.edu.ifsp.scl.sdm.pa2.sdmws.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.sdmws.model.model.Curso
import com.example.sdmws.model.model.Disciplina
import com.example.sdmws.model.model.Semestre
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject

class SdmWsViewModel(application: Application): AndroidViewModel(application) {
    val cursoMld: MutableLiveData<Curso> = MutableLiveData()
    val semestreMld: MutableLiveData<Semestre> = MutableLiveData()
    val disciplinaMld: MutableLiveData<Disciplina> = MutableLiveData()
    private val escopoCorrotinas = CoroutineScope(Dispatchers.IO + Job())
    private val filaRequisicoesVolley: RequestQueue =
        Volley.newRequestQueue(application.baseContext)

    companion object {
        val URL_BASE = "http://nobile.pro.br/sdm_ws"
        val ENDPOINT_CURSO = "/curso"
        val ENDPOINT_SEMESTRE = "/semestre"
        val ENDPOINT_DISCIPLINA = "/disciplina"
    }

    fun getCurso() {
        escopoCorrotinas.launch {
            val urlCurso = "${URL_BASE}${ENDPOINT_CURSO}"
            val requisicaoCursoJor = JsonObjectRequest(
                Request.Method.GET,
                urlCurso,
                null,
                { response ->
                    if (response != null ) {
                        val curso: Curso = jsonToCurso(response)
                        cursoMld.postValue(curso)
                    }
                },
                { error -> Log.e(urlCurso, error.toString()) }
            )
            filaRequisicoesVolley.add(requisicaoCursoJor)
        }
    }
    fun getSemestre(sid: Int) {
        escopoCorrotinas.launch {
            val urlSemestre = "${URL_BASE}${ENDPOINT_SEMESTRE}/$sid"
            val requisicaoSemestreJar = JsonArrayRequest(
                Request.Method.GET,
                urlSemestre,
                null,
                { response ->

                    response?.also { disciplinaJar ->
                        val semestre = Semestre()
                        for (indice in 0 until disciplinaJar.length()){
                            val disciplinaJson = disciplinaJar.getJSONObject(indice)
                            val disciplina = jsonToDisciplina(disciplinaJson)
                            semestre.add(disciplina)
                        }
                        semestreMld.postValue(semestre)
                    }
                },
                { error -> Log.e(urlSemestre, error.toString()) }
            )
            filaRequisicoesVolley.add(requisicaoSemestreJar)
        }

    }
    fun getDisciplina(sigla: String) {

    }

    private fun jsonToCurso(json: JSONObject): Curso {
        val curso: Curso = Curso(
            json.getInt("horas"),
            json.getString("nome"),
            json.getInt("semestres"),
            json.getString("sigla")
        )
        return curso
    }

    private fun jsonToDisciplina(json: JSONObject): Disciplina {
        val disciplina: Disciplina = Disciplina(
            json.getInt("aulas"),
            json.getString("nome"),
            json.getInt("semestres"),
            json.getString("sigla")
        )
        return disciplina
    }
}