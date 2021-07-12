package co.`in`.mzk.stepperview

import co.`in`.mzk.stepper.TimelineAdapter
import co.`in`.mzk.stepper.TimelineStep


class StepAdapter(private val items: List<MyItem>) : TimelineAdapter<StepAdapter.MyItem>() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): MyItem {
        return items[position]
    }


    data class MyItem(val isActive: Boolean,
                      val formattedDate: String,
                      val title: String, val desc : String)

    override fun bindView(timelineStep: TimelineStep, item: MyItem) {

        with(timelineStep) {
            setActive(item.isActive)
            setAnchor(item.formattedDate)
            setSubtitle(item.desc)
//            setSubtitleTextAppearance(R.style.SubTitleText)
//            setAnchorTextAppearance(R.style.AnchorText)
//            setTitleTextAppearance(R.style.Titl)
            setTitle(item.title)


        }
    }

}