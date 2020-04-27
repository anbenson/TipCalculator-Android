package dev.adbenson.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.channels.SeekableByteChannel

const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialization.
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        tvTipAmount.text = "0.0"
        tvTotalAmount.text = "0.0"
        updateTipDescription(INITIAL_TIP_PERCENT)

        // UI callbacks.
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvTipPercent.text = "$progress%"
                updateTipDescription(progress)
                computeTipAndTotal()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        etBase.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                computeTipAndTotal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription

        val color = ArgbEvaluator().evaluate(
            tipPercent / seekBarTip.max.toFloat(),
            ContextCompat.getColor(this, R.color.colorWorstTip),
            ContextCompat.getColor(this, R.color.colorBestTip)
        ) as Int
        tvTipDescription.setTextColor(color)

        val emoji = when (tipPercent) {
            in 0..9 -> "☹️"
            in 10..14 -> "\uD83D\uDE10"
            in 15..19 -> "\uD83D\uDE42"
            in 20..24 -> "\uD83D\uDE00"
            in 25..29 -> "\uD83D\uDE0D"
            else -> "\uD83E\uDD11"
        }
        tvTipEmoji.text = emoji
    }

    private fun computeTipAndTotal() {
        val baseAmount = when (etBase.text.isBlank()) {
            true -> 0.0
            false -> etBase.text.toString().toDouble()
        }
        val tipPercent = seekBarTip.progress
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text =  "%.2f".format(totalAmount)
    }
}
