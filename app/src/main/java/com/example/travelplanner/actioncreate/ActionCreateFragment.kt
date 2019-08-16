package com.example.travelplanner.actioncreate

import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.travelplanner.PlannerApplication
import com.example.travelplanner.R
import com.example.travelplanner.base.TIME_PATTERN
import com.example.travelplanner.base.addSimpleListener
import com.example.travelplanner.base.architecture.Event
import com.example.travelplanner.base.architecture.ShowDatePickerEvent
import com.example.travelplanner.base.architecture.ShowTimePickerEvent
import com.example.travelplanner.base.routing.PlannerRouter
import com.example.travelplanner.base.toReadableString
import kotlinx.android.synthetic.main.fragment_action_create.*

class ActionCreateFragment: Fragment() {

    private var router: PlannerRouter? = null
    private var viewModel: ActionCreateViewModel? = null

    companion object {
        private const val TRIP_ID_KEY = "trip_id"
        fun newInstance(tripId: Long): ActionCreateFragment {
            val result = ActionCreateFragment()
            result.arguments = Bundle().apply { putLong(TRIP_ID_KEY, tripId) }
            return result
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        router = context as? PlannerRouter ?: throw Throwable("Parent activity must implement Router interface!")
        val tripId = arguments?.getLong(TRIP_ID_KEY) ?: throw Exception("Caller must provide tripId!")
        viewModel = ViewModelProviders
            .of(this, ActionCreateViewModelFactory(PlannerApplication.instance!!, lifecycle, tripId))
            .get(ActionCreateViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_action_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.state?.observe(this, Observer { onStateChanged(it) })
        viewModel?.events?.observe(this, Observer { onEventChanged(it) })

        nameField.addSimpleListener { viewModel?.onNameChanged(it.toString()) }
        dateField.setOnClickListener { viewModel?.onDateFieldClicked() }
        startTimeField.setOnClickListener { viewModel?.onStartTimeFieldClicked() }
        endTimeField.setOnClickListener { viewModel?.onEndTimeFieldClicked() }
        actionButton.setOnClickListener { viewModel?.onActionButtonClicked() }
        screenHeader.leftButtonListener = { router?.goBack() }
    }

    private fun onStateChanged(state: ActionCreateState) {
        showError(state.error)
        dateField.text = state.date?.toReadableString() ?: ""
        startTimeField.text =
            if (state.startHours != null && state.startMinutes != null) String.format(TIME_PATTERN, state.startHours, state.startMinutes)
            else ""
        endTimeField.text =
            if (state.endHours != null && state.endMinutes != null) String.format(TIME_PATTERN, state.endHours, state.endMinutes)
            else ""
    }

    private fun onEventChanged(event: Event?) {
        when (event) {
            is ShowDatePickerEvent -> showDatePicker(event)
            is ShowTimePickerEvent -> showTimePicker(event)
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
        val pickerListener =  DatePickerDialog.OnDateSetListener { _, year, month, day -> event.listener.invoke(year, month, day) }
        context?.let {
            DatePickerDialog(it, pickerListener, event.year, event.month, event.day)
                .apply {
                    if (event.minDateMillis != null) datePicker.minDate = event.minDateMillis
                    if (event.maxDateMillis != null) datePicker.maxDate = event.maxDateMillis
                }
                .show()
        }
    }

    private fun showTimePicker(event: ShowTimePickerEvent) {
        val pickerListener =  TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute -> event.listener.invoke(hourOfDay, minute) }
        context?.let {
            TimePickerDialog(it, pickerListener, event.hour, event.minute, true).show()
        }
    }
}

class ActionCreateViewModelFactory(private val application: Application, private val lifecycle: Lifecycle,
                                   private val tripId: Long) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ActionCreateViewModel(application, lifecycle, tripId) as T
    }
}