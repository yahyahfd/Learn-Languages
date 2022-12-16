package fr.uparis.zhou.mobiles_projet

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import fr.uparis.zhou.mobiles_projet.databinding.ItemLayoutBinding

class VH(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder( binding.root ){
    var mots: Mot? = null
}

class MyRecAdapter(private var listMots: MutableList<Mot>) : RecyclerView.Adapter<VH>() {
    private var selectedMot = -1
    private var size = 0

    fun changePrefixe(s: Int){
        size = s
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemLayoutBinding
            .inflate(
                LayoutInflater
                    .from(parent.context), parent, false
            )
        return VH(binding)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder
        if(position % 2 == 0){
            holder.itemView.setBackgroundColor(Color.argb(0.1f, 0.3f, 0.3f, 0.0f))
        }else{
            holder.itemView.setBackgroundColor(Color.argb(0.1f, 0.0f, 0.3f, 0.3f))
        }
        if(position == selectedMot){
            holder.itemView.setBackgroundColor(Color.argb(0.5f, 0.2f, 0.2f, 0.2f))
        }else{
            if(position % 2 == 0){
                holder.itemView.setBackgroundColor(Color.argb(0.1f, 0.3f, 0.3f, 0.0f))
            }else{
                holder.itemView.setBackgroundColor(Color.argb(0.1f, 0.0f, 0.3f, 0.3f))
            }
        }
        with(holder.binding){
            val prefix = listMots[position].mot.substring(0, size)
            val suffix = listMots[position].mot.substring(size)
            val color = src.currentTextColor
            val t = "<font color=#cc0029>$prefix</font> <font color=$color>$suffix</font>"

            mot.text = HtmlCompat.fromHtml(t, HtmlCompat.FROM_HTML_MODE_LEGACY)
            src.text = listMots[position].src
            dst.text = listMots[position].dst
            url.text = listMots[position].url
        }
    }

    override fun getItemCount(): Int = listMots.size

    @SuppressLint("NotifyDataSetChanged")
    fun setMots(mots: List<Mot>) {
        this.listMots.clear()
        this.listMots.addAll(mots)
        notifyDataSetChanged()
        Log.d("Adapter ", "setListMots $listMots")
    }

    fun selected(): Mot {
        return listMots[selectedMot]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun delete(){
        listMots.removeAt(selectedMot)
        selectedMot = -1
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(p: Mot){
        listMots[selectedMot] = p
        notifyDataSetChanged()
    }
}