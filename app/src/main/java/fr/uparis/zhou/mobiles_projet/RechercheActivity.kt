package fr.uparis.zhou.mobiles_projet

//noinspection SuspiciousImport
import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import fr.uparis.zhou.mobiles_projet.databinding.ActivityRechercheBinding
import java.io.File
import java.io.PrintWriter

class RechercheActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityRechercheBinding = ActivityRechercheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ed_mot = binding.edMot
        val ed_src = binding.edSrc
        val ed_dst = binding.edDst
        val creer = binding.creer
        val ed_dico = binding.edDico

        val listDico = mutableListOf<String>()

        val file = File(filesDir, "dico")
        file.reader().use {
            listDico.addAll(it.readLines())
        }

        val spinner = binding.dico
        val adapter = ArrayAdapter(applicationContext, R.layout.simple_spinner_dropdown_item, listDico)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.setOnItemSelectedListener(
            object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    binding.selected.setText("Dictionnaire selectionné : " + adapter.getItem(pos))
                    if(adapter.getItem(pos) == "autre"){
                        creer.setVisibility(View.VISIBLE)
                        ed_dico.setVisibility(View.VISIBLE)
                        ed_dico.setText("")
                    }else{
                        creer.setVisibility(View.INVISIBLE)
                        ed_dico.setVisibility(View.INVISIBLE)
                        ed_dico.setText("")
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        )


        val chercher = binding.chercher
        chercher.setOnClickListener {
            if (spinner.getSelectedItem().toString() == "autre") {
                if(ed_dico.text.toString() != "") {
                    if (!listDico.contains(ed_dico.text.toString())) {
                        listDico.add(ed_dico.text.toString())
                        val f = File(filesDir, "dico")
                        f.appendText(ed_dico.text.toString() + "\n")
                    }
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse("http://www.google.fr/search?q=" + ed_dico.text.toString() + "+" + ed_mot.text.toString() + "+" + ed_src.text.toString() + "+" + ed_dst.text.toString()))
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "Veuillez sélectionner un dictionnaire valide", Toast.LENGTH_LONG).show()
                }
            }else{
                if(spinner.getSelectedItem().toString() == "larousse"){
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse("https://www.larousse.fr/dictionnaires/" + ed_src.text.toString() + "-" + ed_dst.text.toString() + "/" + ed_mot.text.toString()))
                    startActivity(intent)
                }else if(spinner.getSelectedItem().toString() == "reverso"){
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse("http://dictionnaire.reverso.net/" + ed_src.text.toString() + "-" + ed_dst.text.toString() + "/" + ed_mot.text.toString()))
                    startActivity(intent)
                }else if(spinner.getSelectedItem().toString() == "reverso"){
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse("https://dictionary.cambridge.org/fr/dictionnaire/" + ed_src.text.toString() + "-" + ed_dst.text.toString() + "/" + ed_mot.text.toString()))
                    startActivity(intent)
                }else{
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse("http://www.google.fr/search?q=" + ed_dico.text.toString() + "+" + ed_mot.text.toString() + "+" + ed_src.text.toString() + "+" + ed_dst.text.toString()))
                    startActivity(intent)
                }
            }
        }

    }
}