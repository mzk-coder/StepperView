package co.`in`.mzk.stepper

import android.view.View

public abstract class TimelineAdapter<T> where T : Any {

    abstract fun getCount(): Int

    abstract fun getItem(position: Int): T

    abstract fun bindView(timelineStep: TimelineStep, item: T)


}