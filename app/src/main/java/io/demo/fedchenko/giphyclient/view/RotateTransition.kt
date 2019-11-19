package io.demo.fedchenko.giphyclient.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout

class RotateTransition(context: Context, attributeSet: AttributeSet) :
    Transition(context, attributeSet) {
    private var layout: FrameLayout? = null

    override fun captureStartValues(transitionValues: TransitionValues?) {
        if (transitionValues?.view is FrameLayout)
            layout = transitionValues.view as FrameLayout
    }

    override fun captureEndValues(transitionValues: TransitionValues?) {}

    override fun createAnimator(
        sceneRoot: ViewGroup?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator {
        return ValueAnimator.ofFloat(0f, 360f).apply {
            addUpdateListener {
                layout?.rotation = it.animatedValue as Float
            }
        }
    }
}