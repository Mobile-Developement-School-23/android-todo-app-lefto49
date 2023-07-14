package com.todoapplication.view.fragments

import android.animation.ValueAnimator
import android.content.SharedPreferences
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
        fun setBottomSheet(activity: MainActivity) {
            val bottomSheet = BottomSheetDialog(activity)
            bottomSheet.setContentView(R.layout.bottom_sheet)
            val darkButton = bottomSheet.findViewById<RadioButton>(R.id.rb_dark) ?: return
            val lightButton = bottomSheet.findViewById<RadioButton>(R.id.rb_light) ?: return
            val systemButton = bottomSheet.findViewById<RadioButton>(R.id.rb_system) ?: return

            darkButton.setOnClickListener {
                darkButton.isChecked = true
                systemButton.isChecked = false
                lightButton.isChecked = false

                bottomSheet.dismiss()
                activity.setTheme("dark")
            }

            lightButton.setOnClickListener {
                systemButton.isChecked = false
                darkButton.isChecked = false
                lightButton.isChecked = true

                bottomSheet.dismiss()
                activity.setTheme("light")
            }

            systemButton.setOnClickListener {
                darkButton.isChecked = false
                lightButton.isChecked = false
                systemButton.isChecked = true

                bottomSheet.dismiss()
                activity.setTheme("system")
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