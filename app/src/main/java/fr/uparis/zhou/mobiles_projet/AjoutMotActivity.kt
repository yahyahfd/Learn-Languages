    package fr.uparis.zhou.mobiles_projet

    import android.annotation.SuppressLint
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.lifecycle.ViewModelProvider
    import com.google.android.material.internal.ViewUtils.hideKeyboard
    import fr.uparis.zhou.mobiles_projet.databinding.ActivityAjoutMotBinding

    class AjoutMotActivity : AppCompatActivity() {

        private val model by lazy {
            ViewModelProvider(this)[AjoutViewModel::class.java]
        }

        @SuppressLint("RestrictedApi")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val binding: ActivityAjoutMotBinding = ActivityAjoutMotBinding.inflate( layoutInflater )
            setContentView(binding.root)

            if( intent.action.equals( "android.intent.action.SEND" ) ) {
                val url = intent.extras?.getString("android.intent.extra.TEXT")
                val parts = url!!.split("/")
                if(parts[2].contains("larousse") && parts[3] == "dictionnaires"){
                    val langues = parts[4].split("-")
                    if(langues.size == 2){
                        binding.edSrc.setText(langues[0])
                        binding.edDst.setText(langues[1])
                    }else{
                        binding.edSrc.setText(langues[0])
                        binding.edDst.setText(langues[0])
                    }
                    binding.edMot.setText(parts[5])
                }
                else if(parts[2].contains("reverso") && parts[2].contains("dictionnaire")){
                    val langues = parts[3].split("-")
                    if(langues.size == 2){
                        binding.edSrc.setText(langues[0])
                        binding.edDst.setText(langues[1])
                    }else{
                        binding.edSrc.setText(langues[0])
                        binding.edDst.setText(langues[0])
                    }
                    binding.edMot.setText(parts[4])
                }
                else if(parts[2].contains("cambridge") && parts[2].contains("dictionary")){
                    val langues = parts[5].split("-")
                    if(langues.size == 2){
                        binding.edSrc.setText(langues[0])
                        binding.edDst.setText(langues[1])
                    }else{
                        binding.edSrc.setText(langues[0])
                        binding.edDst.setText(langues[0])
                    }
                    binding.edMot.setText(parts[6])
                }
                binding.edUrl.setText(url)
            }

            binding.bAjouter.setOnClickListener{
                var motEmpty = false
                var srcEmpty = false
                var dstEmpty = false
                var urlEmpty = false

                val motText = binding.edMot.text.toString()
                val srcText = binding.edSrc.text.toString()
                val dstText = binding.edDst.text.toString()
                val urlText = binding.edUrl.text.toString()

                if(motText == "") motEmpty = true
                if(srcText == "") srcEmpty = true
                if(dstText == "") dstEmpty = true
                if(urlText == "") urlEmpty = true

                if(motEmpty){
                    binding.edMot.requestFocus()
                    Log.d("pas saisi", "mot")
                } else if(srcEmpty){
                    binding.edSrc.requestFocus()
                    Log.d("pas saisi", "source")
                }else if(dstEmpty){
                    binding.edDst.requestFocus()
                    Log.d("pas saisi", "destinataire")
                }else if(urlEmpty){
                    binding.edUrl.requestFocus()
                    Log.d("pas saisi", "url")
                }else{
                    val ajMot = binding.edMot.text.toString().trim()
                    val ajSrc = binding.edSrc.text.toString().trim()
                    val ajDst = binding.edDst.text.toString().trim()
                    val ajUrl = binding.edUrl.text.toString().trim()

                    model.insert(Mot(ajMot, ajSrc, ajDst, ajUrl))

                    Toast.makeText(this, "Mot ajouté", Toast.LENGTH_LONG).show()
                    binding.edMot.setText("")
                    binding.edSrc.setText("")
                    binding.edDst.setText("")
                    binding.edUrl.setText("")
                    hideKeyboard(currentFocus ?: View(this))
                }
            }
        }
    }