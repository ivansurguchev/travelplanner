package com.example.travelplanner.tripcreate

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.travelplanner.PlannerApplication
import com.example.travelplanner.R
import com.example.travelplanner.base.addSimpleListener
import com.example.travelplanner.base.architecture.Event
import com.example.travelplanner.base.architecture.ShowDatePickerEvent
import com.example.travelplanner.base.toReadableString
import kotlinx.android.synthetic.main.fragment_trip_create.*

class TripCreateFragment: Fragment() {

    private var viewModel: TripCreateViewModel? = null

    override fun onAttach(context: Context?) {
        viewModel = ViewModelProviders
            .of(this, TripCreateViewModelFactory(PlannerApplication.instance!!, lifecycle))
            .get(TripCreateViewModel::class.java)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_trip_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.state?.observe(this, Observer { onStateChanged(it) })
        viewModel?.events?.observe(this, Observer { onEventChanged(it) })
        nameField.addSimpleListener { viewModel?.onNameChanged(it.toString()) }
        startDateField.setOnClickListener { viewModel?.onStartDateFieldClicked() }
        endDateField.setOnClickListener { viewModel?.onEndDateFieldClicked() }
        actionButton.setOnClickListener { viewModel?.onActionButtonClicked() }
        screenHeader.leftButtonListener = { viewModel?.onBackPressed() }
    }

    private fun onStateChanged(state: CreateTripState) {
        showError(state.error)
        startDateField.text = state.startDate?.toReadableString()
        endDateField.text = state.endDate?.toReadableString()
    }

    private fun onEventChanged(event: Event?) {
        when (event) {
            is ShowDatePickerEvent -> showDatePicker(event)
        }
    }

    private fun showError(message: String?) {
        if (message == null) {
            errorText.visibility = View.GONE
        } else {
            errorText.visibility = View.VISIBLE
            errorText.text = message
        }
    }

    private fun showDatePicker(event: ShowDatePickerEvent) {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day -> event.listener.invoke(year, month, day) }
        context?.let {
            DatePickerDialog(it, listener, event.year, event.month, event.day).show()
        }
    }
}