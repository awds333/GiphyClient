package io.demo.fedchenko.giphyclient.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.view.ViewGroup

class RotateTransition(context: Context, attributeSet: AttributeSet) :
    Transition(context, attributeSet) {

    override fun captureStartValues(transitionValues: TransitionValues?) {
        val view = transitionValues?.view ?: return
        if (!view.transitionName.isNullOrEmpty()) {
            val position = IntArray(2)
            view.getLocationOnScreen(position)
            transitionValues.values["startPosition"] = position[0] + (view.width / 2)
            transitionValues.values["transitionName"] = view.transitionName
        }
    }

    override fun captureEndValues(transitionValues: TransitionValues?) {
        val view = transitionValues?.view ?: return
        if (!view.transitionName.isNullOrEmpty()) {
            val position = IntArray(2)
            view.getLocationOnScreen(position)
            transitionValues.values["endPosition"] = position[0] + (view.width / 2)
            transitionValues.values["transitionName"] = view.transitionName
        }
    }

    override fun createAnimator(
        sceneRoot: ViewGroup?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator {
        if (startValues?.values?.get("transitionName") == null)
            return ValueAnimator.ofFloat(0f, 1f)
        val animator =
            if ((startValues.values?.get("startPosition") as Int) < (endValues?.values?.get("endPosition") as Int))
                ValueAnimator.ofFloat(0f, 360f)
            else
                ValueAnimator.ofFloat(360f, 0f)

        animator.addUpdateListener {
            startValues.view?.rotation = it.animatedValue as Float
        }

        return animator
    }
}