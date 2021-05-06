package com.example.transporargo.main_fragments.ui.request_form

import android.annotation.SuppressLint
import com.example.transporargo.R
import com.example.transporargo.main_fragments.interfaces.Validator
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RequestValidator (
    private val evaluateDate: String?,
    private val weight: String?,
    private val capacity: String?,
    private val truckType: String?,
    private val cargoType: String?,
    private val placeFrom: String?,
    private val placeTo: String?,
    private val phone: String?,
    private val isCargo: Boolean
) : Validator {

    override var errorMsgId: Int? = null

    private val dateFormat = "dd.MM.yyyy"

    override fun isValid(): Boolean {
        return isCapacityValid() && isWeightValid() && when {
            isCargo -> isCargoTypeValid()
            else -> isTransportTypeValid()
        } && isEvaluateDateValid() && isPhoneValid() && isPlaceFromValid() && isPlaceToValid()
    }

    private fun isCapacityValid(): Boolean {
        if (capacity.isNullOrEmpty()) {
            errorMsgId = R.string.capacity_input_is_empty
            return false
        }

        if (capacity.toFloatOrNull() == null) {
            errorMsgId = R.string.capacity_is_not_numeric
            return false
        }
        return true
    }

    private fun isWeightValid(): Boolean {
        if (weight.isNullOrEmpty()) {
            errorMsgId = R.string.weight_input_is_empty
            return false
        }

        if (weight.toFloatOrNull() == null) {
            errorMsgId = R.string.weight_is_not_numeric
            return false
        }
        return true
    }

    private fun isCargoTypeValid(): Boolean =
        when {
            cargoType.isNullOrEmpty() -> {
                errorMsgId = R.string.cargo_input_is_empty
                false
            }
            else -> {
                true
            }
        }

    private fun isTransportTypeValid(): Boolean =
        when {
            truckType.isNullOrEmpty() -> {
                errorMsgId = R.string.truck_input_is_empty
                false
            }
            else -> {
                true
            }
        }

    private fun isPhoneValid(): Boolean =
        when {
            phone.isNullOrEmpty() -> {
                errorMsgId = R.string.truck_input_is_empty
                false
            }
            else -> {
                true
            }
        }

    private fun isEvaluateDateValid(): Boolean {
        if (evaluateDate.isNullOrEmpty()) {
            errorMsgId = R.string.evaluate_date_input_is_empty
            return false
        }

        if (!isDateValid()) {
            errorMsgId = R.string.evaluate_date_must_be_in_day_month_year_pattern
            return false
        }

        val cal = Calendar.getInstance()
        cal.time = Date()
        val millisCurrent = cal.timeInMillis

        val sdf = SimpleDateFormat(dateFormat)
        val evaluateDateMillis: Long = try {
            val mDate = sdf.parse(evaluateDate)
            val timeInMilliseconds = mDate?.time
            timeInMilliseconds!!.toLong()
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }

        if (millisCurrent > evaluateDateMillis) {
            errorMsgId = R.string.cant_add_request_for_evaluate_date_from_the_past
            return false
        }
        return true
    }

    @SuppressLint("SimpleDateFormat")
    private fun isDateValid(): Boolean {
        val sdf: DateFormat = SimpleDateFormat(dateFormat)
        sdf.isLenient = false
        return try {
            sdf.parse(evaluateDate.toString())
            true
        } catch (e: ParseException) {
            false
        }
    }

    private fun isPlaceFromValid(): Boolean {
        if (placeFrom.isNullOrEmpty()) {
            errorMsgId = R.string.place_from_is_empty
            return false
        }
        return true
    }

    private fun isPlaceToValid(): Boolean {
        if (placeTo.isNullOrEmpty()) {
            errorMsgId = R.string.place_to_is_empty
            return false
        }
        return true
    }
}