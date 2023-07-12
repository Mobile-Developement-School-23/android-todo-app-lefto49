package com.todoapplication.view.fragments

import android.animation.ValueAnimator
import android.content.res.Resources
import android.view.animation.AccelerateInterpolator
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.todoapplication.R
import com.todoapplication.view.activity.MainActivity

class BottomSheetConfig {
    companion object {
        fun setBottomSheet(activity: MainActivity, importance: TextView, resources: Resources) {
            val bottomSheet = BottomSheetDialog(activity)
            bottomSheet.setContentView(R.layout.bottom_sheet)
            val importantButton = bottomSheet.findViewById<RadioButton>(R.id.rb_high) ?: return
            val lowButton = bottomSheet.findViewById<RadioButton>(R.id.rb_low) ?: return
            val commonButton = bottomSheet.findViewById<RadioButton>(R.id.rb_common) ?: return

            when (importance.text.toString()) {
                resources.getString(R.string.low) -> lowButton.isChecked = true
                resources.getString(R.string.high) -> importantButton.isChecked = true
                else -> commonButton.isChecked = true
            }

            importantButton.setOnClickListener {
                importance.setText(R.string.high)
                commonButton.isChecked = false
                lowButton.isChecked = false

                setAnimation(importantButton, resources)
            }

            lowButton.setOnClickListener {
                importance.setText(R.string.low)
                commonButton.isChecked = false
                importantButton.isChecked = false
            }

            commonButton.setOnClickListener {
                importance.setText(R.string.no)
                importantButton.isChecked = false
                lowButton.isChecked = false
            }
            bottomSheet.show()
        }

        fun setAnimation(importantButton: RadioButton, resources: Resources) {
            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.duration = 500
            animator.interpolator = AccelerateInterpolator()

            animator.addUpdateListener {
                val value = animator.animatedValue as Float
                importantButton.setTextColor(
                    ColorUtils.blendARGB(
                        resources.getColor(R.color.colorOnPrimary, null),
                        resources.getColor(R.color.red, null),
                        value
                    )
                )
            }

            animator.start()

            animator.addUpdateListener {
                val value = animator.animatedValue as Float
                importantButton.setTextColor(
                    ColorUtils.blendARGB(
                        resources.getColor(R.color.red, null),
                        resources.getColor(R.color.colorOnPrimary, null),
                        value
                    )
                )
            }
            animator.start()
        }
    }
}