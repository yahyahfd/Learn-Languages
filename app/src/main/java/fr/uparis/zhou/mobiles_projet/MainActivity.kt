package fr.uparis.zhou.mobiles_projet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.uparis.zhou.mobiles_projet.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate( layoutInflater )
        setContentView(binding.root)

        binding.ajoutMot.setOnClickListener{
            val myIntent = Intent(this@MainActivity, AjoutMotActivity::class.java)
            //myIntent.putExtra("key", value) //Optional parameters
            this@MainActivity.startActivity(myIntent)
        }

        binding.afficherBdd.setOnClickListener{
            val myIntent = Intent(this@MainActivity, AfficherBddActivity::class.java)
            //myIntent.putExtra("key", value) //Optional parameters
            this@MainActivity.startActivity(myIntent)
        }
    }
}