package fr.uparis.zhou.mobiles_projet

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fr.uparis.zhou.mobiles_projet.databinding.ActivityAfficherBddBinding


class AfficherBddActivity : AppCompatActivity() {

    private val model by lazy {
        ViewModelProvider(this)[AjoutViewModel::class.java]
    }
    val adapter = MyRecAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityAfficherBddBinding = ActivityAfficherBddBinding.inflate( layoutInflater )
        setContentView(binding.root)
        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        model.loadAllWords().observe(this) {
            Log.d("mots : ", "nouvelle liste de mots $it")
            adapter.setMots(it)
        }
        recyclerView.adapter = adapter

        val recherche = binding.mot
        recherche.addTextChangedListener (object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                model.loadPartialWorld(s.toString()).observe(this@AfficherBddActivity) {
                    Log.d("mots : ", "nouvelle liste de mots $it")
                    adapter.setMots(it)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        val rechercheSrc = binding.src
        rechercheSrc.addTextChangedListener (object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                model.loadPartialSrc(s.toString()).observe(this@AfficherBddActivity) {
                    Log.d("mots : ", "nouvelle liste de mots $it")
                    adapter.setMots(it)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val rechercheDst = binding.dst
        rechercheDst.addTextChangedListener (object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                model.loadPartialDst(s.toString()).observe(this@AfficherBddActivity) {
                    Log.d("mots : ", "nouvelle liste de mots $it")
                    adapter.setMots(it)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_suppression->{
                if(adapter.selected.size == 0){
                    Toast.makeText(applicationContext, "Aucun élément choisi", Toast.LENGTH_SHORT).show()
                }else{
                    AlertDialog.Builder(this)
                        .setMessage("Confirmation de suppression?")
                        .setPositiveButton("Confirmer"
                        ) { _, _ ->
                            for (mot in adapter.selected) {
                                model.deleteWorld(mot)
                            }
                            Toast.makeText(
                                applicationContext,
                                "Element(s) choisi(s) supprimé(s)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton("Annuler") { _, _ ->
                            Toast.makeText(
                                applicationContext,
                                "Annulation de la suppression",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .show()
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }

}