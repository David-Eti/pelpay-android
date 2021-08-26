package ui.library.maskededittext

import android.content.Context
import android.util.AttributeSet
import com.example.pelpaysamplebuildandroid.R.*
import com.google.android.material.textfield.TextInputEditText


/**
 * Created by Ehigiator David on 20/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
class MaskedEditText(context: Context, attrs: AttributeSet) : TextInputEditText(context, attrs) {

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private var mMaskedFormatter: MaskedFormatter? = null
    private var mMaskedWatcher: MaskedWatcher? = null

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    val maskString: String?
        get() = mMaskedFormatter?.maskString

    val unMaskedText: String?
        get() {
            val currentText = text?.toString()
            val formattedString = currentText?.let { mMaskedFormatter?.formatString(it) }
            return formattedString?.unMaskedString
        }

    init {

        val typedArray = context.obtainStyledAttributes(attrs, styleable.MaskedEditText)

        if (typedArray.hasValue(styleable.MaskedEditText_mask)) {
            val maskStr = typedArray.getString(styleable.MaskedEditText_mask)

            if (maskStr != null && maskStr.isNotEmpty()) {
                setMask(maskStr)
            }
        }

        typedArray.recycle()
    }

    fun setMask(mMaskStr: String) {
        mMaskedFormatter = MaskedFormatter(mMaskStr)

        if (mMaskedWatcher != null) {
            removeTextChangedListener(mMaskedWatcher)
        }

        mMaskedFormatter?.let { mMaskedWatcher = MaskedWatcher(it, this) }
        addTextChangedListener(mMaskedWatcher)
    }

}