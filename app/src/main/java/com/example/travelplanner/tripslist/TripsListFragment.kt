package com.example.travelplanner.tripslist

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
import com.example.travelplanner.base.architecture.LoadingScreenState
import com.example.travelplanner.base.architecture.LoadingState
import kotlinx.android.synthetic.main.fragment_trips_list.*

class TripsListFragment: Fragment() {

    private var viewModel: TripsListViewModel? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders
            .of(this, TripsListViewModelFactory(PlannerApplication.instance!!, lifecycle))
            .get(TripsListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_trips_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tripsList.layoutManager = LinearLayoutManager(context)
        viewModel?.state?.observe(this, Observer { onStateChanged(it) })
        actionButton.setOnClickListener { viewModel?.onCreateNewTripClick() }
    }

    private fun onStateChanged(state: LoadingScreenState<List<TripItem>, String>) {
        when (state.loadingState) {
            LoadingState.LOADING -> showLoading()
            LoadingState.LOADED -> showTrips(state.loadedData)
            LoadingState.ERROR -> showError(state.error)
        }
    }

    private fun showLoading() {
        progressView.visibility = View.VISIBLE
        tripsList.visibility = View.GONE
        errorText.visibility = View.GONE
    }

    private fun showTrips(trips: List<TripItem>?) {
        if (trips?.isEmpty() != false) {
            showError(context?.getString(R.string.trips_list_empty_message))
        } else {
            context?.let {
                tripsList.visibility = View.VISIBLE
                errorText.visibility = View.GONE
                progressView.visibility = View.GONE
                tripsList.adapter = TripsListAdapter(it, trips) { id -> viewModel?.onTripClick(id) }
            }
        }
    }

    private fun showError(message: String?) {
        progressView.visibility = View.GONE
        tripsList.visibility = View.GONE
        errorText.visibility = View.VISIBLE
        errorText.text = message
    }
}