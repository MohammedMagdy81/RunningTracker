package com.magdy.runningapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.magdy.runningapp.R
import com.magdy.runningapp.db.Run
import com.magdy.runningapp.utils.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class RunAdapter :RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    class RunViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val imageView=itemView.findViewById<ImageView>(R.id.ivRunImage)
        val textDate=itemView.findViewById<TextView>(R.id.tvDate)
        val textTime=itemView.findViewById<TextView>(R.id.tvTime)
        val textDistance=itemView.findViewById<TextView>(R.id.tvDistance)
        val textAvgSpeed=itemView.findViewById<TextView>(R.id.tvAvgSpeed)
        val textCalories=itemView.findViewById<TextView>(R.id.tvCalories)
    }

    private val differCallback=object:DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
           return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode()==newItem.hashCode()
        }

    }
    private val differ=AsyncListDiffer(this,differCallback)
   fun submitList(list:List<Run>)=differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
       return RunViewHolder(
           LayoutInflater.from(parent.context)
               .inflate(R.layout.item_run,parent,false)
       )
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
       val run=differ.currentList[position]
        holder.itemView.apply {

            Glide.with(this).load(run.img).into(holder.imageView)

            val calender=Calendar.getInstance().apply {
                timeInMillis=run.timeStamp
            }
            val dateFormat=SimpleDateFormat("dd.MM.yy",Locale.ENGLISH)
            holder.textDate.text=dateFormat.format(calender.time)

            val aveSpeed="${(run.aveSpeedInKMH*10000f).roundToInt() / 10000.0} km/h"
            holder.textAvgSpeed.text=aveSpeed

            val distanceKm="${run.distanceInMeter} m"
            holder.textDistance.text=distanceKm

            holder.textTime.text=TrackingUtility.getFormattedStopWitchTime(run.timeInMilliSecond)

            holder.textCalories.text="${run.caloriesBurned} kcal"
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}