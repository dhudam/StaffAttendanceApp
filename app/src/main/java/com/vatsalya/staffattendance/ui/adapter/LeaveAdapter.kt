package com.vatsalya.staffattendance.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vatsalya.staffattendance.data.entity.LeaveRequest
import com.vatsalya.staffattendance.data.entity.LeaveStatus
import com.vatsalya.staffattendance.databinding.ItemLeaveBinding

class LeaveAdapter(private val onAction: (LeaveRequest, String) -> Unit) :
    ListAdapter<LeaveRequest, LeaveAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemLeaveBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(leave: LeaveRequest) {
            binding.tvLeaveType.text = leave.leaveType.name
            binding.tvDates.text = "${leave.fromDate} → ${leave.toDate}"
            binding.tvReason.text = leave.reason
            binding.tvStatus.text = leave.status.name
            binding.tvStatus.setBackgroundResource(when (leave.status) {
                LeaveStatus.APPROVED -> android.R.color.holo_green_dark
                LeaveStatus.REJECTED -> android.R.color.holo_red_dark
                else -> android.R.color.holo_orange_dark
            })
            if (leave.status == LeaveStatus.PENDING) {
                binding.btnApprove.visibility = View.VISIBLE
                binding.btnReject.visibility = View.VISIBLE
                binding.btnApprove.setOnClickListener { onAction(leave, "approve") }
                binding.btnReject.setOnClickListener { onAction(leave, "reject") }
            } else {
                binding.btnApprove.visibility = View.GONE
                binding.btnReject.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemLeaveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<LeaveRequest>() {
            override fun areItemsTheSame(a: LeaveRequest, b: LeaveRequest) = a.id == b.id
            override fun areContentsTheSame(a: LeaveRequest, b: LeaveRequest) = a == b
        }
    }
}
