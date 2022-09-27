package com.magdy.runningapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.magdy.runningapp.R
import com.magdy.runningapp.db.Run
import com.magdy.runningapp.utils.TrackingUtility
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter :RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    class RunViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

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
            Glide.with(this).load(run.img).into(ivRunImage)
            val calender=Calendar.getInstance().apply {
                timeInMillis=run.timeStamp
            }
            val dateFormat=SimpleDateFormat("dd.MM.yy",Locale.getDefault())
            tvDate.text=dateFormat.format(calender.time)

            val aveSpeed="${run.aveSpeedInKMH} Km/h"
            tvAvgSpeed.text=aveSpeed

            val distanceKm="${run.distanceInMeter/1000}km"
            tvDistance.text=distanceKm

            tvTime.text=TrackingUtility.getFormattedStopWitchTime(run.timeInMilliSecond)
            tvCalories.text="${run.caloriesBurned}kcal"
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}