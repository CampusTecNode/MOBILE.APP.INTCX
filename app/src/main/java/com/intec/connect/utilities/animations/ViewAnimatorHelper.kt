package com.intec.connect.utilities.animations

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.IntegerRes
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.utilities.animations.AnimationConstants.ViewProperties
import com.intec.connect.utilities.animations.ReboundAnimator.ReboundAnimatorType
import kotlin.math.abs

/**
 * This is a helper class that provides methods for view animations.
 */
object ViewAnimatorHelper {
    // Properties.
    // Expand/Collapse animation constants.
    const val ROTATE_180_DEGREE: Int = 180
    private const val EXPAND_DURATION = 300
    private const val EXPAND_DECELERATION = 1f
    private const val COLLAPSE_DURATION = 250
    private const val COLLAPSE_DECELERATION = 0.7f

    // Expand/Collapse animation interpolator.
    private val mExpandInterpolator: Interpolator = DecelerateInterpolator(EXPAND_DECELERATION)
    private val mCollapseInterpolator: Interpolator = DecelerateInterpolator(COLLAPSE_DECELERATION)

    /**
     * Creates an array defining a translate animation for each view.
     *
     * @param translationAnimatorType         TranslationAnimatorType instance.
     * @param translationFromValue            Indicates from where the view should be translated.
     * @param translationToValue              Indicates where the view should be translated.
     * @param animatorDuration                Indicates the duration of the animation.
     * @param startDelayBetweenViewsAnimators Indicates the start delay between each animation.
     * @param viewsToAnimate                  Views to animate.
     * @return Array containing all the animators to play for given views.
     */
    @JvmStatic
    fun getTranslationAnimatorForViews(
        translationAnimatorType: TranslationAnimatorType,
        translationFromValue: Float?, translationToValue: Float?,
        animatorDuration: Int, startDelayBetweenViewsAnimators: Int,
        vararg viewsToAnimate: View?
    ): Array<Animator?> {
        // Create array to allocate all animators.
        val allAnimators = arrayOfNulls<Animator>(viewsToAnimate.size)

        var viewToAnimate: View?

        var translateAnimator: ObjectAnimator

        var duration: Int

        for (index in viewsToAnimate.indices) {
            viewToAnimate = viewsToAnimate[index]

            // Determine animation duration for each view.
            duration = (animatorDuration
                    + (startDelayBetweenViewsAnimators * index))

            // Request a translate animator for each view.
            translateAnimator = getTranslationAnimator(
                translationAnimatorType,
                translationFromValue, translationToValue, viewToAnimate
            )
            translateAnimator.setDuration(duration.toLong())

            // Add each translate animator to the array.
            allAnimators[index] = translateAnimator
        }

        return allAnimators
    }

    /**
     * Plays a translate animation for each view.
     *
     * @param translationAnimatorType         TranslationAnimatorType instance.
     * @param translationFromValue            Indicates from where the view should be translated.
     * @param translationToValue              Indicates where the view should be translated.
     * @param animatorDuration                Indicates the duration of the animation.
     * @param startDelayBetweenViewsAnimators Indicates the start delay between each animation.
     * @param viewsToAnimate                  Views to animate.
     */
    fun playTranslationAnimatorForViews(
        translationAnimatorType: TranslationAnimatorType,
        translationFromValue: Float?, translationToValue: Float?,
        animatorDuration: Int, startDelayBetweenViewsAnimators: Int,
        vararg viewsToAnimate: View?
    ) {
        // Request a translate animator for each view.
        val allAnimators = getTranslationAnimatorForViews(
            translationAnimatorType, translationFromValue,
            translationToValue, animatorDuration,
            startDelayBetweenViewsAnimators, *viewsToAnimate
        )

        // Play animations.
        playAnimators(*allAnimators)
    }

    /**
     * Creates a translation animator for a view.
     *
     * @param translationAnimatorType TranslationAnimatorType instance.
     * @param translationFromValue    Indicates from where the view should be translated.
     * @param translationToValue      Indicates where the view should be translated.
     * @param viewToAnimate           View to animate.
     * @return ObjectAnimator instance configured for the translation animation.
     */
    @JvmStatic
    fun getTranslationAnimator(
        translationAnimatorType: TranslationAnimatorType,
        translationFromValue: Float?, translationToValue: Float?,
        viewToAnimate: View?
    ): ObjectAnimator {
        val translateAnimator =
            if (translationAnimatorType == TranslationAnimatorType.TRANSLATION_X) {
                if (translationFromValue != null) {
                    ObjectAnimator.ofFloat(
                        viewToAnimate,
                        ViewProperties.TRANSLATION_X,
                        translationFromValue, translationToValue!!
                    )
                } else {
                    ObjectAnimator.ofFloat(
                        viewToAnimate,
                        ViewProperties.TRANSLATION_X,
                        translationToValue!!
                    )
                }
            } else {
                if (translationFromValue != null) {
                    ObjectAnimator.ofFloat(
                        viewToAnimate,
                        ViewProperties.TRANSLATION_Y,
                        translationFromValue, translationToValue!!
                    )
                } else {
                    ObjectAnimator.ofFloat(
                        viewToAnimate,
                        ViewProperties.TRANSLATION_Y,
                        translationToValue!!
                    )
                }
            }

        return translateAnimator
    }

    /**
     * Creates a fade animation for given views.
     *
     * @param startDelayBetweenViewsAnimators Indicates the start delay between each animation.
     * @param animatorDuration                Indicates the duration of the animation.
     * @param fadeAnimatorType                FadeAnimatorType instance.
     * @param viewsToAnimate                  Views to animate.
     * @return Array containing all the animators to play for given views.
     */
    @JvmStatic
    fun getFadeAnimatorForViews(
        startDelayBetweenViewsAnimators: Int, animatorDuration: Int,
        fadeAnimatorType: FadeAnimatorType, vararg viewsToAnimate: View?
    ): Array<Animator?> {
        // Create array to allocate all animators.
        val allAnimators = arrayOfNulls<Animator>(viewsToAnimate.size)

        var viewToAnimate: View?

        var duration: Int

        for (index in viewsToAnimate.indices) {
            viewToAnimate = viewsToAnimate[index]

            // Determine animation duration for each view.
            duration = (animatorDuration
                    + (startDelayBetweenViewsAnimators * index))

            /*- Request a translate animator for each view and add each translate animator to
            the array. */
            allAnimators[index] = getFadeAnimator(
                fadeAnimatorType,
                viewToAnimate, duration
            )
        }

        return allAnimators
    }

    /**
     * Plays a fade animation for each view.
     *
     * @param startDelayBetweenViewsAnimators Indicates the start delay between each animation.
     * @param animatorDuration                Indicates the duration of the animation.
     * @param fadeAnimatorType                FadeAnimatorType instance.
     * @param viewsToAnimate                  Views to animate.
     */
    fun playFadeAnimatorForViews(
        startDelayBetweenViewsAnimators: Int, animatorDuration: Int,
        fadeAnimatorType: FadeAnimatorType, vararg viewsToAnimate: View?
    ) {
        // Request a fade animator for each view.
        val allAnimators = getFadeAnimatorForViews(
            startDelayBetweenViewsAnimators, animatorDuration,
            fadeAnimatorType, *viewsToAnimate
        )

        // Play animations.
        playAnimators(*allAnimators)
    }

    /**
     * Creates a fade animator for a view.
     *
     * @param fadeAnimatorType FadeAnimatorType instance.
     * @param viewToAnimate    View to animate.
     * @param animatorDuration Indicates the duration of the fade animation.
     * @return ObjectAnimator instance configured for the fade animation.
     */
    private fun getFadeAnimator(
        fadeAnimatorType: FadeAnimatorType, viewToAnimate: View?,
        animatorDuration: Int
    ): ObjectAnimator {
        val fadeAnimator = if (fadeAnimatorType == FadeAnimatorType.FADE_IN) {
            ObjectAnimator.ofFloat(
                viewToAnimate,
                ViewProperties.ALPHA, 0f, 1f
            )
        } else {
            ObjectAnimator.ofFloat(
                viewToAnimate,
                ViewProperties.ALPHA, 1f, 0f
            )
        }

        fadeAnimator.setDuration(animatorDuration.toLong())

        return fadeAnimator
    }

    /**
     * Creates a rotate animation for given views.
     *
     * @param startDelayBetweenViewsAnimators Indicates the start delay between each animation.
     * @param animatorDuration                Indicates the duration of the animation.
     * @param rotateAnimatorType              RotateAnimatorType instance.
     * @param viewsToAnimate                  Views to animate.
     * @return Array containing all the animators to play for given views.
     */
    private fun getRotateAnimatorForViews(
        startDelayBetweenViewsAnimators: Int, animatorDuration: Int,
        rotateAnimatorType: RotateAnimatorType, vararg viewsToAnimate: View?
    ): Array<Animator?> {
        // Create array to allocate all animators.
        val allAnimators = arrayOfNulls<Animator>(viewsToAnimate.size)

        var rotateAnimator: ObjectAnimator

        var viewToAnimate: View?

        var duration: Int

        for (index in viewsToAnimate.indices) {
            viewToAnimate = viewsToAnimate[index]

            // Request a rotate animator.
            rotateAnimator = getRotateAnimator(
                rotateAnimatorType,
                viewToAnimate
            )

            // Determine animation duration for each view.
            duration = (animatorDuration
                    + (startDelayBetweenViewsAnimators * index))

            // Set animation duration.
            rotateAnimator.setDuration(duration.toLong())

            // Add each rotate animator to the array.
            allAnimators[index] = rotateAnimator
        }

        return allAnimators
    }

    /**
     * Plays a rotate animation for each view.
     *
     * @param startDelayBetweenViewsAnimators Indicates the start delay between each animation.
     * @param animatorDuration                Indicates the duration of the animation.
     * @param rotateAnimatorType              RotateAnimatorType instance.
     * @param viewsToAnimate                  Views to animate.
     */
    fun playRotateAnimatorForViews(
        startDelayBetweenViewsAnimators: Int, animatorDuration: Int,
        rotateAnimatorType: RotateAnimatorType, vararg viewsToAnimate: View?
    ) {
        // Request a rotate animator for each view.
        val allAnimators = getRotateAnimatorForViews(
            startDelayBetweenViewsAnimators, animatorDuration,
            rotateAnimatorType, *viewsToAnimate
        )

        // Play animations.
        playAnimators(*allAnimators)
    }

    /**
     * Generates a rotate animation for a view.
     *
     * @param rotateAnimatorType RotateAnimatorType instance.
     * @param viewToAnimate      View to animate.
     * @return ObjectAnimator instance configured for the rotate animation.
     */
    private fun getRotateAnimator(
        rotateAnimatorType: RotateAnimatorType, viewToAnimate: View?
    ): ObjectAnimator {
        val rotateAnimator = if (rotateAnimatorType == RotateAnimatorType.ROTATE_IN) {
            ObjectAnimator.ofFloat(
                viewToAnimate,
                ViewProperties.ROTATION, 45f, 0f
            )
        } else {
            ObjectAnimator.ofFloat(
                viewToAnimate,
                ViewProperties.ROTATION, 0f, 45f
            )
        }

        return rotateAnimator
    }

    /**
     * Plays a rotate animation for a view.
     *
     * @param viewToAnimate    View to animate.
     * @param animatorDuration Indicates the duration of the rotate animation.
     * @return ObjectAnimator instance used to play rotation animation for the
     * view.
     */
    fun playRotateAnimatorForProgressView(
        viewToAnimate: View?, animatorDuration: Int
    ): ObjectAnimator {
        // Configure ObjectAnimator instance for a rotate animation.
        val rotateAnimator = ObjectAnimator.ofFloat(
            viewToAnimate,
            ViewProperties.ROTATION, 360f, 0f
        )
        rotateAnimator.repeatCount = Animation.INFINITE
        rotateAnimator.setDuration(animatorDuration.toLong())
        rotateAnimator.interpolator = LinearInterpolator()
        rotateAnimator.start()

        return rotateAnimator
    }

    /**
     * Play all animators together.
     *
     * @param animators Animators to play.
     */
    private fun playAnimators(vararg animators: Animator?) {
        val animatorController = AnimatorSet()
        animatorController.playTogether(*animators)
        animatorController.start()
    }

    /**
     * Expands a view.
     *
     * @param mainContainerView    Container on which the view in going to be expanded.
     * @param mainContentView      The container of the whole row.
     * @param expandContainerView  The container to expand.
     * @param contentContainerView The container of the content to show while expanding.
     * @param expandImageView      The image to rotate while expanding.
     * @param viewsToHide          Views to hide while expanding the view. Could be null.
     */
    fun expandView(
        mainContainerView: View, mainContentView: View,
        expandContainerView: View, contentContainerView: View,
        expandImageView: ImageView, vararg viewsToHide: View
    ) {
        // Save the starting height so we can animate from this value.
        val startingHeight = mainContentView.height

        // Set the expand area to visible so we can measure the height to animate to.
        expandContainerView.visibility = View.VISIBLE

        // Add an onPreDrawListener, which gets called after measurement but before the draw.
        val viewTreeObserver = mainContainerView.viewTreeObserver
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // We don't want to continue getting called for every RecyclerView drawing.
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.removeOnPreDrawListener(this)
                }

                // Calculate some values to help with the animation.
                val endingHeight = mainContentView.height
                val distance = endingHeight - startingHeight

                // Set the height back to the start state of the animation.
                mainContentView.layoutParams.height = startingHeight
                mainContentView.requestLayout()

                // Set up the animator to animate the expansion.
                val valueAnimator =
                    ValueAnimator.ofFloat(0f, 1f).setDuration(EXPAND_DURATION.toLong())
                valueAnimator.interpolator = mExpandInterpolator
                valueAnimator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    // For each value from 0 to 1, animate the various parts of the layout.
                    mainContentView.layoutParams.height = (value * distance +
                            startingHeight).toInt()

                    expandImageView.rotation = ROTATE_180_DEGREE * value

                    contentContainerView.alpha = value

                    for (viewToHide in viewsToHide) {
                        viewToHide.alpha = 1 - value
                    }
                    mainContentView.requestLayout()
                }

                // Set everything to their final values when the animation's done.
                valueAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        mainContentView.layoutParams.height =
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        expandImageView.rotation = ROTATE_180_DEGREE.toFloat()
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                })

                valueAnimator.start()

                // Return false so this draw does not occur to prevent the final frame from
                // being drawn for the single frame before the animations start.
                return false
            }
        })
    }

    /**
     * Collapses a view.
     *
     * @param mainContainerView     Container on which the view in going to be collapsed.
     * @param mainContentView       The container of the whole row.
     * @param collapseContainerView The container to collapse.
     * @param contentContainerView  The container of the content to hide while collapsing.
     * @param collapseImageView     The image to rotate while collapsing.
     * @param viewsToShow           Views to show while collapsing the view. Could be null.
     */
    fun collapseView(
        mainContainerView: View, mainContentView: View,
        collapseContainerView: View,
        contentContainerView: View,
        collapseImageView: ImageView,
        vararg viewsToShow: View
    ) {
        // Save the starting height so we can animate from this value.
        val startingHeight = mainContentView.height

        // Set the expand area to gone so we can measure the height to animate to.
        collapseContainerView.visibility = View.GONE

        // Add an onPreDrawListener, which gets called after measurement but before the draw.
        val viewTreeObserver = mainContainerView.viewTreeObserver
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.removeOnPreDrawListener(this)
                }

                // Calculate some values to help with the animation.
                val endingHeight = mainContentView.height
                val distance = endingHeight - startingHeight

                // Re-set the visibilities for the start state of the animation.
                collapseContainerView.visibility = View.VISIBLE

                // Set up the animator to animate the expansion.
                val valueAnimator =
                    ValueAnimator.ofFloat(0f, 1f).setDuration(COLLAPSE_DURATION.toLong())
                valueAnimator.interpolator = mCollapseInterpolator
                valueAnimator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    // For each value from 0 to 1, animate the various parts of the layout.
                    mainContentView.layoutParams.height = (value * distance +
                            startingHeight).toInt()

                    collapseImageView.rotation = ROTATE_180_DEGREE * (1 - value)

                    contentContainerView.alpha = 1 - value

                    for (viewToShow in viewsToShow) {
                        viewToShow.alpha = value
                    }
                    mainContentView.requestLayout()
                }

                // Set everything to their final values when the animation's done.
                valueAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        // Set it back to wrap content since we'd explicitly set the height.
                        mainContentView.layoutParams.height =
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        collapseContainerView.visibility = View.GONE
                        collapseImageView.rotation = 0f
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                })

                valueAnimator.start()

                // Return false so this draw does not occur to prevent the final frame from
                // being drawn for the single frame before the animations start.
                return false
            }
        })
    }

    /**
     * Animate views.
     * <br></br>
     * **Important:** This constructor uses these constants:
     * <br></br>
     *  * **Animation duration:**R.integer.sv_list_view_items_translate_animation_duration
     * <br></br>
     *  * **Delay between views animation:**R.integer.sv_list_view_items_translate_animation_offset
     *
     * @param context             Application context.
     * @param reboundAnimatorType ReboundAnimatorType to use.
     * @param viewsToAnimate      Views to animate.
     * @return AnimatorSet instance used to animate the views.
     */
    fun animateViews(
        context: Activity,
        reboundAnimatorType: ReboundAnimatorType,
        vararg viewsToAnimate: View?
    ): AnimatorSet {
        return animateViews(context, reboundAnimatorType, null, *viewsToAnimate)
    }

    /**
     * Animate views.
     * <br></br>
     * **Important:** This constructor uses these constants:
     * <br></br>
     *  * **Animation duration:**R.integer.sv_list_view_items_translate_animation_duration
     * <br></br>
     *  * **Delay between views animation:**R.integer.sv_list_view_items_translate_animation_offset
     *
     * @param context             Application context.
     * @param reboundAnimatorType ReboundAnimatorType to use.
     * @param animatorListener    AnimatorListener instance to inform the caller of animation states.
     * @param viewsToAnimate      Views to animate.
     * @return AnimatorSet instance used to animate the views.
     */
    private fun animateViews(
        context: Activity,
        reboundAnimatorType: ReboundAnimatorType,
        animatorListener: Animator.AnimatorListener?,
        vararg viewsToAnimate: View
    ): AnimatorSet {
        return animateViews(
            context, reboundAnimatorType, animatorListener,
            R.integer.sv_list_view_items_translate_animation_duration,
            R.integer.sv_list_view_items_translate_animation_offset, *viewsToAnimate
        )
    }

    /**
     * Animate views.
     *
     * @param context             Application context.
     * @param reboundAnimatorType ReboundAnimatorType to use.
     * @param animatorListener    AnimatorListener instance to inform the caller of animation states.
     * @param animatorDuration    Duration of the animation.
     * @param animatorStartDelay  Delay between animations.
     * @param viewsToAnimate      Views to animate.
     * @return AnimatorSet instance used to animate the views.
     */
    private fun animateViews(
        context: Activity,
        reboundAnimatorType: ReboundAnimatorType,
        animatorListener: Animator.AnimatorListener?,
        @IntegerRes animatorDuration: Int,
        @IntegerRes animatorStartDelay: Int,
        vararg viewsToAnimate: View
    ): AnimatorSet {
        // Get animation manager.
        val reboundAnimatorManager = ReboundAnimator(context, reboundAnimatorType)

        // Get animators.
        val allAnimators = reboundAnimatorManager.getReboundAnimatorForViews(
            context.resources.getInteger(animatorDuration),
            context.resources.getInteger(animatorStartDelay), *viewsToAnimate
        )

        // Play animations.
        val animatorController = AnimatorSet()

        if (animatorListener != null) {
            animatorController.addListener(animatorListener)
        }

        animatorController.playTogether(*allAnimators)
        animatorController.start()

        return animatorController
    }

    /**
     * Animates the progress of the ProgressBar from {@param progressBar}'s progress to
     * {@param progress}.
     *
     * @param progressBar ProgressBar to animate progress for.
     * @param progress    Progress to animate.
     */
    fun animateProgressBarProgress(progressBar: ProgressBar, progress: Int) {
        val progressBarAnimator = ObjectAnimator.ofInt(
            progressBar,
            ViewProperties.PROGRESS, progressBar.progress, progress
        )
        progressBarAnimator.setDuration(1000)
        progressBarAnimator.start()
    }

    /**
     * Animates the animation while a user is swiping an element from a list.
     *
     * @param viewHolder ViewHolder to animate.
     * @param dX         The amount of horizontal displacement caused by user's action.
     */
    // TODO: Delete.
    fun performSwipeToDismissAnimation(viewHolder: RecyclerView.ViewHolder, dX: Float) {
        val width = viewHolder.itemView.width.toFloat()
        val alpha = (1.0f - abs(dX.toDouble()) / width).toFloat()

        viewHolder.itemView.alpha = alpha
        viewHolder.itemView.translationX = dX
    }

    /**
     * Defines the fade animations that could be made.
     */
    enum class FadeAnimatorType {
        FADE_IN, FADE_OUT
    }

    /**
     * Defines the rotation animations that could be made.
     */
    enum class RotateAnimatorType {
        ROTATE_IN, ROTATE_OUT
    }

    /**
     * Defines the translate animations that could be made.
     */
    enum class TranslationAnimatorType {
        TRANSLATION_X, TRANSLATION_Y
    }
}