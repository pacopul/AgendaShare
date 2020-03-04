package net.azarquiel.agendashare

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import net.azarquiel.agendashare.model.Amigo
import net.azarquiel.recyclerviewpajaros.adapter.CustomAdapter
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity(), CustomAdapter.OnLongClickListenerAmigo {
    private lateinit var adapter: CustomAdapter
    private var contador: Int=0
    private lateinit var contadorShare: SharedPreferences
    private lateinit var amigosAL: java.util.ArrayList<Amigo>
    private lateinit var agendaShare: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { dialogAmigo(null)}
        initShare()
        initRV()
        showData()
    }


    private fun initShare() {
        // get id last
        contadorShare  = getSharedPreferences("contador", Context.MODE_PRIVATE)
        contador  = contadorShare.getInt("contador", -1)
        if (contador==-1)
            contador=0
        // get amigosAL
        agendaShare = getSharedPreferences("agenda", Context.MODE_PRIVATE)
    }

    private fun initRV() {
        adapter = CustomAdapter(this, R.layout.rowamigo, this)
        rvamigos.adapter = adapter
        rvamigos.layoutManager = LinearLayoutManager(this)
    }


    private fun showData() {
        val amigosShare = agendaShare.all
        amigosAL = ArrayList<Amigo>()
        for (entry in amigosShare.entries) {
            val jsonAmigo = entry.value.toString()
            val amigo = Gson().fromJson(jsonAmigo, Amigo::class.java)
            amigosAL.add(amigo)
        }

        adapter.setAmigos(amigosAL.sortedBy { it.nombre })

    }

    private fun saveAmigoShare(amigo:Amigo) {
        val edit = agendaShare.edit()
        edit.putString("${amigo.id}", Gson().toJson(amigo))
        edit.apply()
    }
    private fun dialogAmigo(amigo: Amigo?) {
        alert {
            title = if (amigo==null) "New Amigo" else "Modify Amigo"
            customView {
                verticalLayout {
                    lparams(width = wrapContent, height = wrapContent)
                    val etNombre = editText {
                        hint = "Nombre"
                        padding = dip(16)
                        amigo?.let{ this.setText(amigo.nombre)}
                    }
                    val etTlf = editText {
                        hint = "Teléfono"
                        padding = dip(16)
                        amigo?.let{ this.setText(amigo.tlf)}
                    }
                    val etEmail = editText {
                        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        hint = "Email"
                        padding = dip(16)
                        amigo?.let{ this.setText(amigo.email)}
                    }
                    positiveButton("Aceptar") {
                        if (etNombre.text.toString().isEmpty() || etTlf.text.toString().isEmpty() || etEmail.text.toString().isEmpty())
                            toast("Campos Obligatorios")
                        else {
                            if (amigo==null) { //new
                                contador++
                                val amigo = Amigo(
                                    contador,
                                    etNombre.text.toString().toUpperCase(),
                                    etTlf.text.toString(),
                                    etEmail.text.toString()
                                )
                                saveAmigoShare(amigo)
                                saveContador()
                                amigosAL.add(amigo)
                            }
                            else { // modify
                                val amigoModify = Amigo(
                                    amigo.id,
                                    etNombre.text.toString().toUpperCase(),
                                    etTlf.text.toString(),
                                    etEmail.text.toString()
                                )
                                amigosAL.remove(amigo)
                                amigosAL.add(amigoModify)
                                saveAmigoShare(amigoModify)
                            }
                            adapter.setAmigos(amigosAL.sortedBy { it.nombre })
                        }

                    }
                    negativeButton("Cancelar"){}
                }
            }
        }.show()
    }

    private fun saveContador() {
        val edit = contadorShare.edit()
        edit.putInt("contador", contador)
        edit.apply()
    }
    // Modify amigo
    fun onClickAmigo(v: View){
        val amigo = v.tag as Amigo
        //Toast.makeText(this, amigo.toString(),Toast.LENGTH_SHORT).show()
        dialogAmigo(amigo)

    }

    override fun OnLongClickAmigo(amigo: Amigo):Boolean{
        alert("¿Estás seguro eliminar a ${amigo.nombre}?", "Confirm") {
            yesButton {
                amigosAL.remove(amigo)
                adapter.setAmigos(amigosAL.sortedBy { it.nombre })
                deleteAmigoShare(amigo)
            }
            noButton {}
        }.show()
        return true
    }

    private fun deleteAmigoShare(amigo: Amigo) {
        val edit = agendaShare.edit()
        edit.remove(amigo.id.toString())
        edit.apply()
    }

}
