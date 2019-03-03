package com.cameron.passwordgenerator

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : BottomSheetDialogFragment() {

    lateinit var listener: SettingsChangedListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = context as SettingsChangedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context missing OnCheckboxSelected interface")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container)

        val sharedPreferences = context?.getSharedPreferences(context?.packageName, Context.MODE_PRIVATE)!!
        val keys = listOf(
            getString(R.string.pref_use_numbers),
            getString(R.string.pref_use_upper),
            getString(R.string.pref_use_lower)
        )

        val useNumbers = sharedPreferences.getBoolean(keys[0], resources.getBoolean(R.bool.use_numbers))
        val useUppercase = sharedPreferences.getBoolean(keys[1], resources.getBoolean(R.bool.use_upper))
        val useLowercase = sharedPreferences.getBoolean(keys[2], resources.getBoolean(R.bool.use_lower))
        val passwordLength = sharedPreferences.getInt(
            getString(R.string.pref_pass_length),
            resources.getInteger(R.integer.default_pass_length)
        )

        val views = listOf(view.includeNumbers, view.includeUpper, view.includeLower)
        val prefs = listOf(useNumbers, useUppercase, useLowercase)

        val sliderMin = resources.getInteger(R.integer.min_pass_length)
        val sliderMax = resources.getInteger(R.integer.max_pass_length)
        val sliderRange = sliderMax - sliderMin

        with(view.slider) {
            positionListener = { pos ->
                bubbleText = "${sliderMin + (sliderRange * pos).toInt()}"
            }

            endTrackingListener = {
                val sliderValue = sliderMin + (sliderRange * position).toInt()

                listener.onLengthChanged(sliderValue)

                sharedPreferences.edit().putInt(
                    getString(R.string.pref_pass_length),
                    sliderValue
                ).apply()
            }
            position = (passwordLength.toFloat() - sliderMin) / sliderRange
        }

        views.forEachIndexed { index, v ->
            v.isChecked = prefs[index]

            v.setOnCheckedChangeListener { _, isChecked ->
                listener.onCheckboxSelected(views[0].isChecked, views[1].isChecked, views[2].isChecked)
                sharedPreferences.edit().putBoolean(keys[index], isChecked).apply()
            }
        }
        return view
    }

    interface SettingsChangedListener {
        fun onCheckboxSelected(useNumbers: Boolean, useUpper: Boolean, useLower: Boolean)
        fun onLengthChanged(passwordLength: Int)
    }
}