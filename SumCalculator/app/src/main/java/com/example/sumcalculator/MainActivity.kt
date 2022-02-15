package com.example.sumcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.text.isDigitsOnly

class MainActivity : AppCompatActivity() {
    private var output: TextView? = null
    private var curText: String = "0"
    private var expr: Expression = Expression()
    private var replaceNum: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        output = findViewById(R.id.output)
    }

    fun numberInput(view: View) {
        if (view is Button) {
            val buttonText = view.text.toString()
            if (buttonText.isDigitsOnly()) {
                if (!replaceNum && curText != "0")
                    curText += buttonText
                else
                    curText = buttonText

                replaceNum = false

                output?.text = curText

            }
            else when (buttonText) {
                "C" -> {
                    expr = Expression()
                    curText = "0"
                    output?.text = curText
                }
                "+", "-", "*" -> {
                    expr.append(curText.toInt(), buttonText)
                    curText += buttonText
                    output?.text = curText
                    curText = "0"
                    replaceNum = false
                }
                "=" -> if (!replaceNum && !expr.isEmpty()){
                    expr.append(curText.toInt(), buttonText)
                    curText = expr.solve().toString()
                    output?.text = curText
                    expr = Expression()
                    replaceNum = true
                }
            }
        }
    }
}