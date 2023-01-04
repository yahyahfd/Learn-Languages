package fr.uparis.zhou.mobiles_projet

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import fr.uparis.zhou.mobiles_projet.databinding.ActivityParamBinding

class ParamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParamBinding
    lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        //on load les préférences dans nos inputs si ils sont présents
        val nbMots = preferences.getInt("nbMots", -1)
        val freqMots = preferences.getInt("freqMots", -1)
        //si -1, on laisse vide, sinon on met les valeurs dans les inputs
        // 0 est une valeur permise? pour l'instant en tout cas oui
        if (nbMots != -1) binding.inputNbMots.setText(nbMots.toString())
        if (freqMots != -1) binding.inputNbFreq.setText(freqMots.toString())

        val lundi = preferences.getString("lundi", "")
        val mardi = preferences.getString("mardi", "")
        val mercredi = preferences.getString("mercredi", "")
        val jeudi = preferences.getString("jeudi", "")
        val vendredi = preferences.getString("vendredi", "")
        val samedi = preferences.getString("samedi", "")
        val dimanche = preferences.getString("dimanche", "")

        if (lundi != "") binding.lundi.setText(lundi)
        if (mardi != "") binding.mardi.setText(mardi)
        if (mercredi != "") binding.mercredi.setText(mercredi)
        if (jeudi != "") binding.jeudi.setText(jeudi)
        if (vendredi != "") binding.vendredi.setText(vendredi)
        if (samedi != "") binding.samedi.setText(samedi)
        if (dimanche != "") binding.dimanche.setText(dimanche)

        val nbmaitrise = preferences.getInt("nbmaitrise", -1)
        val nbappris = preferences.getInt("nbappris", -1)
        val tmpsappris = preferences.getInt("tmpsappris", -1)

        if (nbmaitrise != -1) binding.inputNbMaitrise.setText(nbmaitrise.toString())
        if (nbappris != -1) binding.inputNbAppris.setText(nbappris.toString())
        if (tmpsappris != -1) binding.inputNbTemps.setText(tmpsappris.toString())

    }

    // On sauvegarde dans préférence les valeurs reçu dans les inputs de l'activity
    fun saveParam(view: View) {
        Log.d("save", "sauvegarde")
        //On rajoute un try catch une fois qu'on aura saisi les erreurs possibles
        val nbMots = binding.inputNbMots.text.toString()
        val freqMots = binding.inputNbFreq.text.toString()
        val nbmaitrise = binding.inputNbMaitrise.text.toString()
        val nbappris = binding.inputNbAppris.text.toString()
        val tmpsappris = binding.inputNbTemps.text.toString()

        val lundi = binding.lundi.text.toString()
        val mardi = binding.mardi.text.toString()
        val mercredi = binding.mercredi.text.toString()
        val jeudi = binding.jeudi.text.toString()
        val vendredi = binding.vendredi.text.toString()
        val samedi = binding.samedi.text.toString()
        val dimanche = binding.dimanche.text.toString()
        val editor = preferences.edit()

        if(nbMots.isNotEmpty()){
            editor.putInt("nbMots", nbMots.toInt())
        }else{
            editor.remove("nbMots")
        }
        if(freqMots.isNotEmpty()){
            editor.putInt("freqMots", freqMots.toInt())
        }else{
            editor.remove("freqMots")
        }

        if (checkFormat(lundi) && checkFormat(mardi) && checkFormat(mercredi) &&
                checkFormat(jeudi) && checkFormat(vendredi) && checkFormat(samedi) &&
                checkFormat(dimanche)
        ) {

            if(tmpsappris.isNotEmpty()){
                editor.putInt("tmpsappris", tmpsappris.toInt())
            }else{
                editor.remove("tmpsappris")
            }
            editor.putString("lundi", lundi)
            editor.putString("mardi", mardi)
            editor.putString("mercredi", mercredi)
            editor.putString("jeudi", jeudi)
            editor.putString("vendredi", vendredi)
            editor.putString("samedi", samedi)
            editor.putString("dimanche", dimanche)

            if(nbappris.isNotEmpty() && nbmaitrise.isNotEmpty()){
                editor.putInt("nbappris", nbappris.toInt())
                editor.putInt("nbmaitrise", nbmaitrise.toInt())
                if (nbappris.toInt() > nbmaitrise.toInt()) {
                    editor.apply()
                    //Les paramètres sont sauvegardés dans les préférences,
                    // on doit les load dans l'activity
                    finish()
                } else {
                    Toast.makeText(
                            this,
                            "Le nombre de swipe pour l'apprentissage doit-être " +
                                    "supérieur à celui pour la maitrise.",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                editor.remove("nbappris")
                editor.remove("nbmaitrise")
                editor.apply()
                //Les paramètres sont sauvegardés dans les préférences,
                // on doit les load dans l'activity
                finish()
            }
        } else {
            Toast.makeText(
                    this,
                    " Les champs doivent être \"src-dst\" ou vides" +
                            " (pour charger toutes les langues)",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkFormat(s: String): Boolean {
        return (s.isEmpty() || s.split("-").size == 2)
    }
}