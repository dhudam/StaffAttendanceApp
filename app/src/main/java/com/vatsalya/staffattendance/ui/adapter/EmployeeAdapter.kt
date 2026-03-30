package com.vatsalya.staffattendance.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vatsalya.staffattendance.data.entity.Employee
import com.vatsalya.staffattendance.databinding.ItemEmployeeBinding

class EmployeeAdapter(private val onClick: (Employee) -> Unit) :
    ListAdapter<Employee, EmployeeAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemEmployeeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(emp: Employee) {
            binding.tvEmpName.text = emp.name
            binding.tvEmpCode.text = emp.employeeCode
            binding.tvDept.text = "${emp.department} · ${emp.designation}"
            binding.tvShift.text = emp.shift.name
            if (emp.photoPath.isNotEmpty()) {
                Glide.with(binding.ivPhoto).load(emp.photoPath).circleCrop().into(binding.ivPhoto)
            }
            binding.root.setOnClickListener { onClick(emp) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Employee>() {
            override fun areItemsTheSame(a: Employee, b: Employee) = a.id == b.id
            override fun areContentsTheSame(a: Employee, b: Employee) = a == b
        }
    }
}
