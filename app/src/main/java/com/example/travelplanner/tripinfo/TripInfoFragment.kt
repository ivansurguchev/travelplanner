package com.example.travelplanner.tripinfo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelplanner.PlannerApplication
import com.example.travelplanner.R
import com.example.travelplanner.tripslist.TripItem
import kotlinx.android.synthetic.main.fragment_trip_info.*

class TripInfoFragment: Fragment() {

    private var viewModel: TripInfoViewModel? = null
    private var adapter: ActionsAdapter? = null

    companion object {
        private const val TRIP_ID_KEY = "trip_id"
        fun newInstance(tripId: Long): TripInfoFragment {
            val result = TripInfoFragment()
            result.arguments = Bundle().apply { putLong(TRIP_ID_KEY, tripId) }
            return result
        }
    }

    override fun onAttach(context: Context?) {
        val tripId = arguments?.getLong(TRIP_ID_KEY) ?: throw Exception("Caller must provide tripId!")
        viewModel = ViewModelProviders
            .of(this, TripInfoViewModelFactory(PlannerApplication.instance!!, tripId, lifecycle))
            .get(TripInfoViewModel::class.java)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_trip_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ActionsAdapter(context!!)
        actionsList.layoutManager = LinearLayoutManager(context)
        actionsList.adapter = adapter
        viewModel?.tripSatate?.observe(this, Observer { onTripChanged(it) })
        viewModel?.actionsState?.observe(this, Observer { onActionsChanged(it) })
        actionButton.setOnClickListener { viewModel?.onCreateActionClicked() }
        screenHeader.leftButtonListener = { viewModel?.onBackPressed() }
    }

    private fun onTripChanged(trip: TripItem) {
        screenHeader.primaryText = trip.name
        screenHeader.secondaryText = trip.primaryText
    }

    private fun onActionsChanged(actions: List<Item>) {
        if (actions.isNotEmpty()) {
            adapter?.items = actions
            actionsList.visibility = View.VISIBLE
            noActionsTitle.visibility = View.GONE
        } else {
            actionsList.visibility = View.GONE
            noActionsTitle.visibility = View.VISIBLE
        }
    }
}

