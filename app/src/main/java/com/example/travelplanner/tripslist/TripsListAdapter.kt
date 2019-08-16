package com.example.travelplanner.tripslist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelplanner.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_trip.*

class TripsListAdapter(context: Context, private val trips: List<TripItem>,
                       private val clickListener: (Long?) -> Unit) : RecyclerView.Adapter<TripViewHolder>() {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = TripViewHolder(
        inflater.inflate(R.layout.item_trip, parent, false),
        clickListener
    )

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind((trips[position]))
    }

    override fun getItemCount() = trips.size
}

class TripViewHolder(itemView: View, private val clickListener: (Long?) -> Unit)
    : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun bind(trip: TripItem) {
        itemView.setOnClickListener { clickListener.invoke(trip.objectId) }
        tripName.text = trip.name
        tripPrimaryText.text = trip.primaryText
        tripSecondaryText.text = trip.secondaryText
    }
}