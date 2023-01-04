package fr.uparis.zhou.mobiles_projet

//noinspection SuspiciousImport
import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.uparis.zhou.mobiles_projet.databinding.ActivityRechercheBinding
import java.io.File

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

        val src = binding.src
        src.setOnClickListener {
            Toast.makeText(applicationContext, "Langue source à utiliser (doit être écrit en francais)", Toast.LENGTH_SHORT).show()
        }
        src.setOnLongClickListener {
            if(ed_mot.text.toString().contains(" ")) {
                Toast.makeText(applicationContext, "Langue supporté :\n-francais\n-anglais\n-arabe\n-espagnol\n-portugais\n-italien\n-allemand", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext, "Langue supporté : celle du dictionnaire choisi", Toast.LENGTH_SHORT).show()
            }
            true
        }

        val dst = binding.dst
        dst.setOnClickListener {
            Toast.makeText(applicationContext, "Langue destinataire à utiliser (doit être écrit en francais)", Toast.LENGTH_SHORT).show()
        }
        dst.setOnLongClickListener {
            if(ed_mot.text.toString().contains(" ")) {
                Toast.makeText(applicationContext, "Langue supporté :\n-francais\n-anglais\n-arabe\n-espagnol\n-portugais\n-italien\n-allemand", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext, "Langue supporté : celle du dictionnaire choisi", Toast.LENGTH_SHORT).show()
            }
            true
        }

        val listDico = mutableListOf<String>()

        val file = File(filesDir, "dico")
        file.reader().use {
            listDico.addAll(it.readLines())
        }

        val spinner = binding.dico
        val adapter = ArrayAdapter(applicationContext, R.layout.simple_spinner_dropdown_item, listDico)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        ed_mot.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if(ed_mot.text.toString().contains(" ")){
                    binding.selected.setVisibility(View.INVISIBLE)
                    spinner.setVisibility(View.INVISIBLE)
                    creer.setVisibility(View.INVISIBLE)
                    ed_dico.setVisibility(View.INVISIBLE)
                }else{
                    binding.selected.setVisibility(View.VISIBLE)
                    spinner.setVisibility(View.VISIBLE)
                    creer.setVisibility(View.VISIBLE)
                    ed_dico.setVisibility(View.VISIBLE)
                }
            }
        })

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
                    if(spinner.getSelectedItem().toString() == "autre"){
                        if(!ed_mot.text.toString().contains(" ")){
                            creer.setVisibility(View.VISIBLE)
                            ed_dico.setVisibility(View.VISIBLE)
                            ed_dico.setText("")
                        }
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
            val intent = Intent(Intent.ACTION_VIEW)
            if(ed_mot.text.toString().contains(" ")) {
                if(langue(ed_src.text.toString()) != "null" && langue(ed_dst.text.toString()) != "null"){
                    intent.setData(Uri.parse("https://translate.google.fr/?hl=fr&sl=" + langue(ed_src.text.toString()) + "&tl=" + langue(ed_dst.text.toString()) + "&text=" + ed_mot.text.toString() + "&op=translate"))
                }

            }else{
                if (spinner.getSelectedItem().toString() == "autre") {
                    if(ed_dico.text.toString() != "") {
                        if (!listDico.contains(ed_dico.text.toString())) {
                            listDico.add(ed_dico.text.toString())
                            val f = File(filesDir, "dico")
                            f.appendText(ed_dico.text.toString() + "\n")
                        }
                        intent.setData(Uri.parse("http://www.google.fr/search?q=" + ed_dico.text.toString() + "+" + ed_mot.text.toString() + "+" + ed_src.text.toString() + "+" + ed_dst.text.toString()))
                    }else{
                        Toast.makeText(this, "Veuillez sélectionner un dictionnaire valide", Toast.LENGTH_LONG).show()
                    }
                }else{
                    if(spinner.getSelectedItem().toString() == "larousse"){
                        intent.setData(Uri.parse("https://www.larousse.fr/dictionnaires/" + ed_src.text.toString() + "-" + ed_dst.text.toString() + "/" + ed_mot.text.toString()))
                    }else if(spinner.getSelectedItem().toString() == "reverso"){
                        intent.setData(Uri.parse("http://dictionnaire.reverso.net/" + ed_src.text.toString() + "-" + ed_dst.text.toString() + "/" + ed_mot.text.toString()))
                    }else if(spinner.getSelectedItem().toString() == "cambridge"){
                        intent.setData(Uri.parse("https://dictionary.cambridge.org/fr/dictionnaire/" + ed_src.text.toString() + "-" + ed_dst.text.toString() + "/" + ed_mot.text.toString()))
                    }else{
                        intent.setData(Uri.parse("http://www.google.fr/search?q=" + ed_dico.text.toString() + "+" + ed_mot.text.toString() + "+" + ed_src.text.toString() + "+" + ed_dst.text.toString()))
                    }
                }
            }
            startActivity(intent)
        }
    }

    @Suppress("UNUSED_EXPRESSION")
    fun langue(l: String) : String{
        when(l){
            "francais" -> return "fr"
            "anglais" -> return "en"
            "arabe" -> return "ar"
            "espagnol" -> return "es"
            "portugais" -> return "pt"
            "italien" -> return "it"
            "allemand" -> return "de"
        }
        return "null"
    }
}