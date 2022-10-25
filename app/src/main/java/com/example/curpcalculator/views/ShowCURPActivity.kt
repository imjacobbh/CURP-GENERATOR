package com.example.curpcalculator.views

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.curpcalculator.R
import com.example.curpcalculator.databinding.ActivityShowCurpBinding
import com.example.curpcalculator.logic.models.FieldsCURP
import com.example.curpcalculator.logic.view_models.GenerateCURPViewModel

class ShowCURPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowCurpBinding
    private lateinit var viewModel: GenerateCURPViewModel

    private lateinit var progressDialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowCurpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.appBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.imageButtonCopy.setOnClickListener {
            copyCurpToClipboard()
        }
        viewModel = ViewModelProvider(this)[GenerateCURPViewModel::class.java]
        viewModel.flowState.observe(this, ::onGenerateCURPFlowState)
        progressDialog = CustomProgressDialog(this)


        intent.extras?.let {
            val curpFields = it.get("curpFields") as FieldsCURP
            viewModel.generateCURP(curpFields)
        }
    }

    private fun copyCurpToClipboard() {
        val clip = ClipData.newPlainText(
            getString(R.string.curp),
            binding.textViewCURP.text.toString()
        )
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, getString(R.string.curp_copied), Toast.LENGTH_SHORT).show()
    }

    private fun onGenerateCURPFlowState(state: GenerateCURPViewModel.GenerateCURPFlowState) {
        when (state) {
            GenerateCURPViewModel.GenerateCURPFlowState.OnLoading -> {
                progressDialog.showCustomProgressDialog("Procesando")
            }
            is GenerateCURPViewModel.GenerateCURPFlowState.OnCURPGenerated -> {
                binding.textViewCURP.text = state.curp.uppercase()
                progressDialog.dismissCustomProgressDialog()
                binding.layout.visibility = View.VISIBLE
                Toast.makeText(this, getString(R.string.curp_generated), Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        fun starActivityWithCURPFields(context: Context, curpFields: FieldsCURP) {
            val intent = Intent(context, ShowCURPActivity::class.java)
            intent.putExtra("curpFields", curpFields)
            context.startActivity(intent)
        }
    }
}