package com.vatsalya.staffattendance.ui.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.vatsalya.staffattendance.data.entity.LeaveRequest
import com.vatsalya.staffattendance.data.entity.LeaveStatus
import com.vatsalya.staffattendance.data.entity.LeaveType
import com.vatsalya.staffattendance.databinding.FragmentLeaveBinding
import com.vatsalya.staffattendance.ui.adapter.LeaveAdapter
import com.vatsalya.staffattendance.viewmodel.LeaveViewModel
import java.util.*

class LeaveFragment : Fragment() {
    private var _binding: FragmentLeaveBinding? = null
    private val binding get() = _binding!!
    private val vm: LeaveViewModel by viewModels()
    private lateinit var adapter: LeaveAdapter
    private val ADMIN_PIN = "1234"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentLeaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LeaveAdapter { leave, action ->
            when (action) {
                "approve" -> verifyPin { vm.updateStatus(leave.id, LeaveStatus.APPROVED, "Approved by admin") }
                "reject" -> verifyPin { vm.updateStatus(leave.id, LeaveStatus.REJECTED, "Rejected by admin") }
            }
        }
        binding.recyclerLeaves.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLeaves.adapter = adapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> vm.pendingLeaves.observe(viewLifecycleOwner) { adapter.submitList(it) }
                    1 -> vm.allLeaves.observe(viewLifecycleOwner) { adapter.submitList(it) }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        vm.pendingLeaves.observe(viewLifecycleOwner) { adapter.submitList(it) }

        vm.pendingCount.observe(viewLifecycleOwner) { count ->
            binding.tabLayout.getTabAt(0)?.text = "Pending ($count)"
        }

        binding.fabApplyLeave.setOnClickListener { showApplyLeaveDialog() }
    }

    private fun showApplyLeaveDialog() {
        // Simplified: opens a dialog to apply leave
        val cal = Calendar.getInstance()
        val today = "${cal.get(Calendar.YEAR)}-${"%02d".format(cal.get(Calendar.MONTH)+1)}-${"%02d".format(cal.get(Calendar.DAY_OF_MONTH))}"
        val leave = LeaveRequest(
            employeeId = 1L, // In production this comes from the logged-in employee session
            leaveType = LeaveType.CASUAL,
            fromDate = today,
            toDate = today,
            reason = "Personal work"
        )
        vm.applyLeave(leave)
        Toast.makeText(context, "Leave applied for $today", Toast.LENGTH_SHORT).show()
    }

    private fun verifyPin(onSuccess: () -> Unit) {
        val input = EditText(requireContext()).apply { hint = "Enter Admin PIN" }
        AlertDialog.Builder(requireContext())
            .setTitle("Admin Verification")
            .setView(input)
            .setPositiveButton("Confirm") { _, _ ->
                if (input.text.toString() == ADMIN_PIN) onSuccess()
                else Toast.makeText(context, "Wrong PIN", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
