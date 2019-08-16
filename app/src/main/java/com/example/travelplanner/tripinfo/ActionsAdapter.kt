package com.example.travelplanner.tripinfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelplanner.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_action.*
import kotlinx.android.synthetic.main.item_action_day.*

class ActionsAdapter(context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var items: List<Item> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemType.values()[viewType]) {
            ItemType.DAY -> DayViewHolder(
                inflater.inflate(
                    R.layout.item_action_day,
                    parent,
                    false
                )
            )
            ItemType.ACTION -> ActionViewHolder(
                inflater.inflate(R.layout.item_action, parent, false)
            )
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].type.ordinal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ActionViewHolder -> holder.bind(items[position] as ActionItem)
            is DayViewHolder -> holder.bind(items[position] as DayItem)
        }
    }
}

class ActionViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(item: ActionItem) {
        actionTime.text = item.timeText
        primaryText.text = item.primaryText
        secondaryText.text = item.secondaryText
    }
}

class DayViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(item: DayItem) {
        dayText.text = item.dayText
    }
}