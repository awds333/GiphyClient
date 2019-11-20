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

    private var startPosition: Int = 0
    private var endPosition: Int = 0

    override fun captureStartValues(transitionValues: TransitionValues?) {
        val view = transitionValues?.view
        if (view is FrameLayout) {
            layout = transitionValues.view as FrameLayout
            val position = IntArray(2)
            view.getLocationOnScreen(position)
            startPosition = position[0] + (view.width / 2)
        }
    }

    override fun captureEndValues(transitionValues: TransitionValues?) {
        val view = transitionValues?.view
        if (view is FrameLayout) {
            val position = IntArray(2)
            view.getLocationOnScreen(position)
            endPosition = position[0] + (view.width / 2)
        }
    }

    override fun createAnimator(
        sceneRoot: ViewGroup?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator {
        return (if (startPosition < endPosition) ValueAnimator.ofFloat(0f, 360f)
        else ValueAnimator.ofFloat(360f, 0f)).apply {
            addUpdateListener {
                layout?.rotation = it.animatedValue as Float
            }
        }
    }
}