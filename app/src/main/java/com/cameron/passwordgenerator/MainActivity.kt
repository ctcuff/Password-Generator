package com.cameron.passwordgenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SettingsFragment.SettingsChangedListener {

    private val tag = MainActivity::javaClass.name
    private var useNumbers = true
    private var useUpper = true
    private var useLower = true
    private var passwordLength = 0
    private val interpolator = OvershootInterpolator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sharedPrefs = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        passwordLength = sharedPrefs.getInt(
            getString(R.string.pref_pass_length),
            resources.getInteger(R.integer.default_pass_length)
        )

        useNumbers = sharedPrefs.getBoolean(
            getString(R.string.pref_use_numbers),
            resources.getBoolean(R.bool.use_numbers)
        )
        useUpper = sharedPrefs.getBoolean(
            getString(R.string.pref_use_upper),
            resources.getBoolean(R.bool.use_upper)
        )
        useLower = sharedPrefs.getBoolean(
            getString(R.string.pref_use_lower),
            resources.getBoolean(R.bool.use_lower)
        )

        passwordTv.text = if (savedInstanceState == null) {
            PasswordUtils.generatePassword(useNumbers, useUpper, useLower, passwordLength)
        } else {
            savedInstanceState.getString(tag)
        }

        bottomAppBar.replaceMenu(R.menu.menu_app_bar)
        bottomAppBar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_settings) {
                val fragment = SettingsFragment()
                fragment.show(supportFragmentManager, fragment.tag)
            }
            if (item.itemId == R.id.action_copy) {
                copyToClipboard()
            }
            true
        }

        fabGenerate.setOnClickListener {
            passwordTv.text = PasswordUtils.generatePassword(useNumbers, useUpper, useLower, passwordLength)
            ViewCompat.animate(fabGenerate)
                .rotationBy(-90f)
                .withLayer()
                .setDuration(300)
                .setInterpolator(interpolator)
                .start()
        }

        passwordTv.setOnLongClickListener {
            copyToClipboard()
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(tag, passwordTv.text as String)
    }

    override fun onCheckboxSelected(useNumbers: Boolean, useUpper: Boolean, useLower: Boolean) {
        this.useNumbers = useNumbers
        this.useUpper = useUpper
        this.useLower = useLower
    }

    override fun onLengthChanged(passwordLength: Int) {
        this.passwordLength = passwordLength
    }

    private fun copyToClipboard() {
        val clipboard = baseContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data = ClipData.newPlainText("Password", passwordTv.text)

        clipboard.primaryClip = data
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}
