package fr.uparis.zhou.mobiles_projet

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import fr.uparis.zhou.mobiles_projet.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate( layoutInflater )
        setContentView(binding.root)

        val file = File(filesDir, "dico")
        if (file.length() == 0L) {
            file.appendText("autre\n")
            file.appendText("larousse\n")
            file.appendText("reverso\n")
            file.appendText("cambridge\n")
        }

        binding.ajoutMot.setOnClickListener{
            val myIntent = Intent(this@MainActivity, AjoutMotActivity::class.java)
            this@MainActivity.startActivity(myIntent)
        }

        binding.afficherBdd.setOnClickListener{
            val myIntent = Intent(this@MainActivity, AfficherBddActivity::class.java)
            this@MainActivity.startActivity(myIntent)
        }

        binding.chercher.setOnClickListener{
            val myIntent = Intent(this@MainActivity, RechercheActivity::class.java)
            this@MainActivity.startActivity(myIntent)
        }
        //On lance le service de notifs
        val intent = Intent(this, ApprentissageService::class.java)
        startService(intent)
    }


    fun params(view: View) {
        val myIntent = Intent(this@MainActivity, ParamActivity::class.java)
        this@MainActivity.startActivity(myIntent)
    }
}