package co.`in`.mzk.stepperview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.`in`.mzk.stepper.TimelineLayout
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list : ArrayList<StepAdapter.MyItem> = ArrayList()


        list.add(StepAdapter.MyItem(false, "time", "Order placed", "Desc "))
        list.add(StepAdapter.MyItem(false, "time", "Order placed 2", "Desc "))
        list.add(StepAdapter.MyItem(true, "time", "Order placed 3", "Desc "))

        val stepAdapter = StepAdapter(list)
        val layout = findViewById<TimelineLayout>(R.id.timeline_layout)
        layout.setAdapter(stepAdapter)


        
    }
}