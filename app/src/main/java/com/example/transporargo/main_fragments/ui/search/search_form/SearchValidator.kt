package com.example.transporargo.main_fragments.ui.search.search_form

import android.annotation.SuppressLint
import com.example.transporargo.R
import com.example.transporargo.main_fragments.interfaces.Validator
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SearchValidator(
    private val evaluateDate: String?,
    private val weight: String?,
    private val capacity: String?,
    private val truckType: String?,
    private val cargoType: String?,
    private val placeFrom: String?,
    private val placeTo: String?
) : Validator {

    override var errorMsgId: Int? = null

    private val dateFormat = "dd.MM.yyyy"

    override fun isValid(): Boolean {
        return isFormNotEmpty() && isCapacityValid() && isWeightValid() && isEvaluateDateValid()
    }

    private fun isCapacityValid(): Boolean {
        if (!capacity.isNullOrEmpty() && capacity.toFloatOrNull() == null) {
            errorMsgId = R.string.capacity_is_not_numeric
            return false
        }
        return true
    }

    private fun isWeightValid(): Boolean {
        if (!weight.isNullOrEmpty() && weight.toFloatOrNull() == null) {
            errorMsgId = R.string.weight_is_not_numeric
            return false
        }
        return true
    }

    private fun isEvaluateDateValid(): Boolean {
        if (evaluateDate.isNullOrEmpty()) {
            return true
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

    private fun isFormNotEmpty(): Boolean {
        return if (!placeTo.isNullOrEmpty() || !placeFrom.isNullOrEmpty() || !evaluateDate.isNullOrEmpty()
            || !truckType.isNullOrEmpty() || !cargoType.isNullOrEmpty() || !weight.isNullOrEmpty()
            || !capacity.isNullOrEmpty()
        ) {
            true
        } else {
            errorMsgId = R.string.search_form_is_empty
            false
        }
    }
}