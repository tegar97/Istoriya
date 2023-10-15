package com.tegar.istoriya.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.tegar.istoriya.R

class CustomInput: TextInputEditText, View.OnTouchListener {

    private lateinit var clearButtonImage: Drawable
    var isPasswordInput: Boolean = false
    var isEmailInput: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.baseline_close_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isPasswordInput && s.toString().length < 8) {
                    setError("Password tidak boleh kurang dari 8 karakter", null)
                }else if(isEmailInput && !isEmailValid(s.toString())){
                    setError("Format email tidak valid", null)
                }
                else {
                    error = null
                }

            }
            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }
    private fun isEmailValid(email: CharSequence): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        return email.matches(emailPattern.toRegex())
    }




    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }
}