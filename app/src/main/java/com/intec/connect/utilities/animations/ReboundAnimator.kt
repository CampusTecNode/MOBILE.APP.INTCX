package com.intec.connect.utilities.animations

import android.animation.Animator
import android.app.Activity
import android.view.View
import com.intec.connect.R
import com.intec.connect.utilities.ScreenDimensions.getHeightPixels
import com.intec.connect.utilities.ScreenDimensions.getWidthPixels
import com.intec.connect.utilities.animations.ViewAnimatorHelper.TranslationAnimatorType
import com.intec.connect.utilities.animations.ViewAnimatorHelper.getFadeAnimatorForViews
import com.intec.connect.utilities.animations.ViewAnimatorHelper.getTranslationAnimator
import com.intec.connect.utilities.animations.ViewAnimatorHelper.getTranslationAnimatorForViews

/**
 * This class provides a rebound animation for views.
 */
class ReboundAnimator @JvmOverloads constructor(
    context: Activity,
    reboundDirection: ReboundAnimatorType = ReboundAnimatorType.RIGHT_TO_LEFT
) {
    /**
     * Defines the animation state.
     */
    private enum class ReboundAnimationToPlay {
        FIRST_REBOUND, SECOND_REBOUND
    }

    /**
     * Defines the animation to play.
     */
    enum class ReboundAnimatorType {
        RIGHT_TO_LEFT, BOTTOM_TO_TOP
    }

    private var context: Activity? = null
    private var translationAnimatorType: TranslationAnimatorType? = null

    /**
     * Constructor.
     *
     * @param context          Application context.
     * @param reboundDirection Indicates direction of rebound animation.
     */
    /**
     * Constructor.
     *
     * @param context Application context.
     */
    init {
        initializeVariables(context)
        setTranslationAnimatorTypeForReboundDirection(reboundDirection)
    }

    /**
     * Initializes class level variables.
     *
     * @param context Application context.
     */
    private fun initializeVariables(context: Activity) {
        this.context = context
    }

    /**
     * Determines which direction the rebound animator should be made.
     *
     * @param reboundDirection Direction for the rebound animation.
     */
    private fun setTranslationAnimatorTypeForReboundDirection(reboundDirection: ReboundAnimatorType) {
        if (ReboundAnimatorType.RIGHT_TO_LEFT == reboundDirection) {
            setTranslationAnimatorType(TranslationAnimatorType.TRANSLATION_X)
        } else {
            setTranslationAnimatorType(TranslationAnimatorType.TRANSLATION_Y)
        }
    }

    /**
     * Sets the animation translation direction to use.
     *
     * @param translationAnimatorType TranslationAnimatorType instance indicating translate
     * animation direction.
     */
    private fun setTranslationAnimatorType(translationAnimatorType: TranslationAnimatorType) {
        this.translationAnimatorType = translationAnimatorType
    }

    private val translationFromValue: Float
        /**
         * Determines the fromValue to use for the translate animation.
         *
         * @return Value to use for the translate animation fromValue property.
         */
        get() {
            /*- If translation animation type is "TRANSLATION_X", we need to use the width of the
        screen, otherwise, use the height. */
            val translationFromValue =
                if (translationAnimatorType == TranslationAnimatorType.TRANSLATION_X) getWidthPixels(
                    context!!
                ).toFloat()
                else {
                    getHeightPixels(context!!).toFloat()
                }

            return translationFromValue
        }

    /**
     * Creates a rebound animation for a view.
     *
     * @param viewToAnimate View to animate.
     * @return Animator array containing the animations to play for a rebound
     * animation.
     */
    fun getReboundAnimatorForView(viewToAnimate: View?): Array<Animator> {
        // Get translation animator.
        val translateAnimator = getTranslationAnimatorForViews(
            translationAnimatorType!!,
            translationFromValue, 0.0f, 0,
            0, viewToAnimate
        )
        translateAnimator[0]!!.addListener(
            MyAnimatorListener(
                viewToAnimate,
                ReboundAnimationToPlay.FIRST_REBOUND
            )
        )

        // Get fade animator.
        val fadeAnimator = getFadeAnimatorForViews(
            0, 0, ViewAnimatorHelper.FadeAnimatorType.FADE_IN,
            viewToAnimate
        )

        // Concatenate translation and fade animators into a single array.
        return AnimatorUtil.concatAnimators(translateAnimator, fadeAnimator)
    }

    /**
     * Creates a rebound animation for one or more views.
     *
     * @param animatorDuration                The duration of the rebound animation.
     * @param startDelayBetweenViewsAnimators Animation start delay between views animation.
     * @param viewsToAnimate                  Views to animate.
     * @return Animator array containing the animations to play for a rebound
     * animation for each view.
     */
    fun getReboundAnimatorForViews(
        animatorDuration: Int,
        startDelayBetweenViewsAnimators: Int,
        vararg viewsToAnimate: View?
    ): Array<Animator> {
        // Get translation animator.
        val translateAnimator = getTranslationAnimatorForViews(
            translationAnimatorType!!,
            translationFromValue, 0.0f, animatorDuration,
            startDelayBetweenViewsAnimators, *viewsToAnimate
        )

        // Set the animation listener.
        for (index in translateAnimator.indices) {
            translateAnimator[index]
                ?.addListener(
                    MyAnimatorListener(
                        viewsToAnimate[index],
                        ReboundAnimationToPlay.FIRST_REBOUND
                    )
                )
        }

        // Get fade animator.
        val fadeAnimator = getFadeAnimatorForViews(
            startDelayBetweenViewsAnimators, animatorDuration,
            ViewAnimatorHelper.FadeAnimatorType.FADE_IN, *viewsToAnimate
        )

        // Concatenate translation and fade animators into a single array.
        return AnimatorUtil.concatAnimators(translateAnimator, fadeAnimator)
    }

    /**
     * This AnimatorListener is used to perform a rebound animation.
     */
    private inner class MyAnimatorListener(// The view to animate.
        private val viewToAnimate: View?,
        // Rebound animation to play.
        private val reboundAnimationToPlay: ReboundAnimationToPlay
    ) : Animator.AnimatorListener {
        override fun onAnimationCancel(arg0: Animator) {
        }

        override fun onAnimationEnd(arg0: Animator) {
            when (reboundAnimationToPlay) {
                ReboundAnimationToPlay.FIRST_REBOUND -> {
                    val translateAnimator = getTranslationAnimator(
                        translationAnimatorType!!,
                        null, context!!.resources.getDimensionPixelSize(
                            R.dimen.rebound_distance
                        ).toFloat(), viewToAnimate
                    )
                    translateAnimator.addListener(
                        MyAnimatorListener(
                            viewToAnimate, ReboundAnimationToPlay.SECOND_REBOUND
                        )
                    )
                    translateAnimator.start()
                }

                ReboundAnimationToPlay.SECOND_REBOUND -> {
                    val translateAnimator = getTranslationAnimator(
                        translationAnimatorType!!,
                        null, 0.0f, viewToAnimate
                    )
                    translateAnimator.start()
                }
            }
        }

        override fun onAnimationRepeat(arg0: Animator) {
        }

        override fun onAnimationStart(arg0: Animator) {
        }
    }
}