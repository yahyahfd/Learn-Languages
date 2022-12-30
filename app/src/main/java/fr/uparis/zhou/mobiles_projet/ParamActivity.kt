package fr.uparis.zhou.mobiles_projet

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import fr.uparis.zhou.mobiles_projet.databinding.ActivityParamBinding
import fr.uparis.zhou.mobiles_projet.databinding.ActivityRechercheBinding

class ParamActivity : AppCompatActivity() {
    lateinit var binding: ActivityParamBinding
    lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = getSharedPreferences("preferences",Context.MODE_PRIVATE)
        //on load les préférences dans nos inputs si ils sont présents
        val nbMots = preferences.getInt("nbMots", -1)
        val freqMots = preferences.getInt("freqMots", -1)
        //si -1, on laisse vide, sinon on met les valeurs dans les inputs
        // 0 est une valeur permise? pour l'instant en tout cas oui
        if (nbMots != -1) binding.inputNbMots.setText(nbMots.toString())
        if (freqMots != -1) binding.inputNbFreq.setText(freqMots.toString())
    }

    fun saveParam(view: View) { // On sauvegarde dans préférence les valeurs reçu dans les deux inputs de l'activity
        Log.d("save", "sauvegarde")
        //On rajoute un try catch une fois qu'on aura saisi les erreurs possibles
        try {
            val nbMots = binding.inputNbMots.text.toString().toInt()
            val freqMots = binding.inputNbFreq.text.toString().toInt()
            val editor = preferences.edit()
            editor.putInt("nbMots", nbMots)
            editor.putInt("freqMots", freqMots)
            editor.apply()
            //Les paramètres sont sauvegardés dans les préférences, on doit les load dans l'activity de tests (plus tard)
            finish()
        } catch (e: NumberFormatException) {
            println(e.message)
            Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show()
        }
    }
}