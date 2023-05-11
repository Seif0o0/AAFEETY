package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.R

class MyFamousOrderProductsAdapter :
    RecyclerView.Adapter<MyFamousOrderProductsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val group = itemView.findViewById<Group>(R.id.pharmacy_name_group)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.my_pharmacy_order_product_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.group.visibility = View.GONE
    }

    override fun getItemCount() = 10
}