package fr.uparis.zhou.mobiles_projet

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fr.uparis.zhou.mobiles_projet.databinding.ActivityAfficherBddBinding
import fr.uparis.zhou.mobiles_projet.databinding.ActivityAjoutMotBinding

class AfficherBddActivity : AppCompatActivity() {

    private val model by lazy {
        ViewModelProvider(this)[AjoutViewModel::class.java]
    }
    val adapter = MyRecAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityAfficherBddBinding = ActivityAfficherBddBinding.inflate( layoutInflater )
        setContentView(binding.root)

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
                adapter.changePrefixe(s.length)
                model.loadPartialWorld(s.toString()).observe(this@AfficherBddActivity) {
                    Log.d("mots : ", "nouvelle liste de mots $it")
                    adapter.setMots(it)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

}