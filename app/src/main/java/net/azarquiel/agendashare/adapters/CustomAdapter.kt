package net.azarquiel.recyclerviewpajaros.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rowamigo.view.*
import net.azarquiel.agendashare.model.Amigo

/**
 * Created by pacopulido on 9/10/18.
 */
class CustomAdapter(val context: Context,
                    val layout: Int,
                    val listener: OnLongClickListenerAmigo
                    ) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private var dataList: List<Amigo> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item,listener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setAmigos(amigos: List<Amigo>) {
        this.dataList = amigos
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(
            dataItem: Amigo,
            listener: OnLongClickListenerAmigo
        ){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem
            itemView.tvnombre.text = dataItem.nombre
            itemView.tvmail.text = dataItem.email
            itemView.tvtlf.text = dataItem.tlf

            itemView.tag = dataItem
            itemView.setOnLongClickListener{
                listener.OnLongClickAmigo(dataItem)
            }
        }
    }
    interface OnLongClickListenerAmigo {
        fun OnLongClickAmigo(amigo: Amigo):Boolean{
            return true
        }
    }

}