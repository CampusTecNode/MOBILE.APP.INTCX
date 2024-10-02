package com.intec.connect.utilities.animations

import android.animation.Animator
import android.animation.AnimatorSet
import android.app.Activity
import android.os.SystemClock
import android.util.SparseArray
import android.view.View
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import kotlin.math.max

/**
 * This is a helper class for animating rows inside a ListView.
 */
class ListViewAnimatorHelper {
    /**
     * The active Animators. Keys are hashcodes of the Views that are animated.
     */
    private val mAnimators = SparseArray<Animator>()

    // Variables.
    private var lastAnimatedPosition = 0
    private var firstAnimatedPosition = 0
    private var animationDuration = 0
    private var animationDelay = 0
    private var animationInitialDelay = 0
    private var animationStart: Long = 0

    // Views.
    private var listView: ListView? = null
    private var linearLayoutManager: LinearLayoutManager? = null

    // Application context.
    private val context: Activity

    /**
     * Constructor.
     *
     * @param context  Application context.
     * @param listView ListView instance in which rows are going to be animated.
     */
    constructor(context: Activity, listView: ListView?) {
        this.context = context
        this.listView = listView

        initializeVariables()
    }

    /**
     * Constructor.
     *
     * @param context       Application context.
     * @param layoutManager Layout Manager which determines how rows are being displayed.
     */
    constructor(context: Activity, layoutManager: RecyclerView.LayoutManager?) {
        this.context = context
        this.linearLayoutManager = layoutManager as LinearLayoutManager?

        initializeVariables()
    }

    /**
     * Initializes class level variables.
     */
    private fun initializeVariables() {
        firstAnimatedPosition = -1
        lastAnimatedPosition = firstAnimatedPosition

        animationStart = -1

        setAnimationDuration(
            context.resources.getInteger(
                R.integer.sv_list_view_items_translate_animation_duration
            )
        )

        setAnimationDelay(
            context.resources.getInteger(
                R.integer.sv_list_view_items_translate_animation_offset
            )
        )

        setAnimationInitialDelay(
            context.resources.getInteger(
                R.integer.sv_list_view_items_translate_animation_initial_delay
            )
        )
    }

    // Setters.
    private fun setAnimationDuration(animationDuration: Int) {
        this.animationDuration = animationDuration
    }

    private fun setAnimationDelay(animationDelay: Int) {
        this.animationDelay = animationDelay
    }

    private fun setAnimationInitialDelay(animationInitialDelay: Int) {
        this.animationInitialDelay = animationInitialDelay
    }

    /**
     * Animates given View if necessary.
     *
     * @param position the position of the item the View represents.
     * @param view     the View that should be animated.
     */
    fun animateViewIfNecessary(
        position: Int, view: View,
        animators: Array<Animator?>
    ) {
        if (position > lastAnimatedPosition) {
            if (firstAnimatedPosition == -1) {
                firstAnimatedPosition = position
            }

            cancelExistingAnimation(view)

            animateView(position, view, animators)

            lastAnimatedPosition = position
        }
    }

    /**
     * Animates given View.
     *
     * @param view the View that should be animated.
     */
    fun animateView(
        position: Int, view: View,
        animators: Array<Animator?>
    ) {
        if (animationStart == -1L) {
            animationStart = SystemClock.uptimeMillis()
        }

        view.alpha = 0f

        val set = AnimatorSet()
        set.playTogether(*animators)
        set.startDelay = calculateAnimationDelay(position).toLong()
        set.setDuration(animationDuration.toLong())
        set.start()

        mAnimators.put(view.hashCode(), set)
    }

    /**
     * Returns the delay in milliseconds after which animation for View with
     * position mLastAnimatedPosition + 1 should start.
     */
    private fun calculateAnimationDelay(position: Int): Int {
        val delay: Int

        val lastVisiblePosition =
            if (listView != null) listView!!.lastVisiblePosition else linearLayoutManager!!.findLastVisibleItemPosition()

        val firstVisiblePosition =
            if (listView != null) listView!!.firstVisiblePosition else linearLayoutManager!!.findFirstVisibleItemPosition()

        val numberOfItemsOnScreen = lastVisiblePosition - firstVisiblePosition

        val numberOfAnimatedItems = position - 1 - firstAnimatedPosition

        if (numberOfItemsOnScreen + 1 < numberOfAnimatedItems) {
            delay = animationDelay
        } else {
            val delaySinceStart = (position - firstAnimatedPosition) * animationDelay

            delay = max(
                0.0, (-SystemClock.uptimeMillis() + animationStart
                        + animationInitialDelay + delaySinceStart).toInt().toDouble()
            ).toInt()
        }

        return delay
    }

    /**
     * Cancels any existing animations for given View.
     */
    private fun cancelExistingAnimation(view: View) {
        val hashCode = view.hashCode()

        val animator = mAnimators[hashCode]

        if (animator != null) {
            animator.end()

            mAnimators.remove(hashCode)
        }
    }

    /**
     * Resets animated positions.
     */
    fun resetAnimatedPositions() {
        firstAnimatedPosition = -1
        lastAnimatedPosition = firstAnimatedPosition

        animationStart = -1
    }
}