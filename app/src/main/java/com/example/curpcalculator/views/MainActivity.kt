package com.example.curpcalculator.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.curpcalculator.R
import com.example.curpcalculator.databinding.ActivityMainBinding
import com.example.curpcalculator.logic.models.FieldsCURP
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*
import java.util.regex.Pattern
import java.util.regex.Pattern.MULTILINE

@SuppressLint(
    "ClickableViewAccessibility",
    "SimpleDateFormat",
    "ClickableViewAccessibility",
    "NonConstantResourceId"
)
class MainActivity : AppCompatActivity(), View.OnTouchListener,
    View.OnFocusChangeListener, AdapterView.OnItemSelectedListener, TextWatcher {

    private lateinit var binding: ActivityMainBinding


    //region VIEWS
    private lateinit var editTextName: EditText
    private lateinit var editTextFirstLastName: EditText
    private lateinit var editTextBirthDate: EditText

    private val dataPicker by lazy {
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(getText(R.string.select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
    }
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupViews()
    }

    private fun setupViews() {

        editTextBirthDate = binding.editTextDate
        editTextFirstLastName = binding.editTextFirstLastName
        editTextName = binding.editTextName

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_text,
            R.id.item_text_view,
            resources.getStringArray(R.array.state_list)
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown)
        binding.textFieldState.adapter = adapter

        editTextBirthDate.setOnTouchListener(this)
        editTextName.addTextChangedListener(this)
        editTextBirthDate.onFocusChangeListener = this
        editTextBirthDate.addTextChangedListener(this)
        editTextName.onFocusChangeListener = this
        editTextFirstLastName.onFocusChangeListener = this
        editTextFirstLastName.addTextChangedListener(this)
        binding.textFieldState.onFocusChangeListener = this
        binding.textFieldState.setOnTouchListener(this)
        binding.textFieldState.onItemSelectedListener = this
        dataPicker.addOnPositiveButtonClickListener {
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utc.timeInMillis = it
            val stringData =

                "${
                    utc.get(
                        Calendar.YEAR
                    )
                }/${
                    if (utc.get(Calendar.MONTH) + 1 < 10) "0" + (utc.get(Calendar.MONTH) + 1) else utc.get(
                        Calendar.MONTH
                    ) + 1
                }/${
                    if (utc.get(Calendar.DAY_OF_MONTH) < 10) "0" + utc.get(Calendar.DAY_OF_MONTH) else utc.get(
                        Calendar.DAY_OF_MONTH
                    )
                }"
            binding.textFieldName.error = null
            binding.textFieldName.isErrorEnabled = false
            editTextBirthDate.setText(stringData)
        }

        binding.buttonCalculate.setOnClickListener {
            openActivityToShowCURPCalculated()
        }

        binding.appBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.about -> {
                    AboutActivity.startActivity(this)
                    true
                }
                else -> false
            }
        }
    }

    private fun openActivityToShowCURPCalculated() {
        val nombre = editTextName.text.toString()
        val primerApellido = editTextFirstLastName.text.toString()
        val segundoApellido = binding.textFieldSegundoApellido.editText?.text.toString()
        val sexo: Char =
            if (binding.radioGroup.checkedRadioButtonId == R.id.radio_button_1) 'M' else 'H'
        val fechaNacimiento = editTextBirthDate.text.toString()
        val estadoNacimiento =
            resources.getStringArray(R.array.state_list_ab)[binding.textFieldState.selectedItemPosition]

        val CURPFields = FieldsCURP(
            nombre,
            primerApellido,
            segundoApellido,
            sexo,
            fechaNacimiento,
            estadoNacimiento
        )

        ShowCURPActivity.starActivityWithCURPFields(this, CURPFields)


    }

    override fun onTouch(v: View, event: MotionEvent?): Boolean {
        if (v.id == R.id.editTextDate) {
            if (dataPicker.dialog == null && !dataPicker.isVisible)
                dataPicker.show(supportFragmentManager, "materialDatePicker")
        }
        return false
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        validateView()
        if (v.id == R.id.editTextDate && hasFocus) {
            if (dataPicker.dialog == null && !dataPicker.isVisible)
                dataPicker.show(supportFragmentManager, "materialDatePicker")
        }
    }

    private fun validateView() {
        var invalid = false
        if (editTextName.text.toString().trim().isEmpty()) {
            binding.textFieldName.error = getString(R.string.name_required)
            invalid = true
            binding.buttonCalculate.isEnabled = false
        } else {
            binding.textFieldName.error = null
            binding.textFieldName.isErrorEnabled = false
        }

        if (editTextFirstLastName.text.toString().trim().isEmpty()) {
            binding.textFieldApellido.error = getString(R.string.first_lastname_required)
            invalid = true
        } else {
            binding.textFieldApellido.error = null
            binding.textFieldApellido.isErrorEnabled = false
        }
        if (binding.textFieldState.selectedItemPosition == 0) {
            invalid = true
            binding.errorSpinner.visibility = View.VISIBLE
        } else {
            binding.errorSpinner.visibility = View.GONE
        }

        val ptr = Pattern.compile(
            "^\\d{4}\\/(0[1-9]|1[012])\\/(0[1-9]|[12][0-9]|3[01])\$",
            MULTILINE
        )
        if (!ptr.matcher(editTextBirthDate.text.toString().trim()).find()) {
            binding.textFieldBirthDate.error = getString(R.string.birthdate_required)
            binding.textFieldBirthDate.isErrorEnabled = true
            invalid = true
        } else {
            binding.textFieldBirthDate.error = null
            binding.textFieldBirthDate.isErrorEnabled = false
        }
        if (binding.radioGroup.checkedRadioButtonId == -1)
            invalid = true

        if (binding.textFieldState.selectedItemPosition == 0) {
            invalid = true
        }
        binding.buttonCalculate.isEnabled = !invalid

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        validateView()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        validateView()
    }

    override fun afterTextChanged(s: Editable?) {
    }
}