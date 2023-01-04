package fr.uparis.zhou.mobiles_projet

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fr.uparis.zhou.mobiles_projet.databinding.ItemLayoutBinding


class MyRecAdapter(var listMots: MutableList<Mot>) : RecyclerView.Adapter<MyRecAdapter.VH>() {

    val selected = mutableListOf<Mot>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VH, position: Int) {
        if(position % 2 == 0){
            holder.itemView.setBackgroundColor(Color.argb(0.1f, 0.3f, 0.3f, 0.0f))
        }else{
            holder.itemView.setBackgroundColor(Color.argb(0.1f, 0.0f, 0.3f, 0.3f))
        }
        if(selected.contains(listMots[position])){
            holder.itemView.setBackgroundColor(Color.argb(105, 105, 105, 105))
        }
        holder.itemView.setOnClickListener {

            var color = Color.TRANSPARENT
            val background: Drawable = holder.itemView.getBackground()
            if (background is ColorDrawable) color = background.color
            if(color == Color.argb(105, 105, 105, 105)){
                if(selected.contains(listMots[holder.absoluteAdapterPosition]))
                    selected.remove(listMots[holder.absoluteAdapterPosition])
                if(position % 2 == 0){
                    holder.itemView.setBackgroundColor(Color.argb(0.1f, 0.3f, 0.3f, 0.0f))
                }else{
                    holder.itemView.setBackgroundColor(Color.argb(0.1f, 0.0f, 0.3f, 0.3f))
                }
            }else{
                if(!selected.contains(listMots[holder.absoluteAdapterPosition]))
                    selected.add(listMots[holder.absoluteAdapterPosition])
                holder.itemView.setBackgroundColor(Color.argb(105, 105, 105, 105))
            }
        }
        with(holder.binding){
            mot.text = listMots[position].mot
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

    class VH(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder( binding.root ){
        var mots: Mot? = null
    }
}