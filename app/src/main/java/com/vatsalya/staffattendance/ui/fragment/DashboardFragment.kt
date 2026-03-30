package com.vatsalya.staffattendance.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.vatsalya.staffattendance.databinding.FragmentDashboardBinding
import com.vatsalya.staffattendance.viewmodel.DashboardViewModel

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val vm: DashboardViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDate.text = vm.today

        vm.totalActive.observe(viewLifecycleOwner) { binding.tvTotal.text = it.toString() }
        vm.presentToday.observe(viewLifecycleOwner) { binding.tvPresent.text = it.toString() }
        vm.lateToday.observe(viewLifecycleOwner) { binding.tvLate.text = it.toString() }
        vm.pendingLeaves.observe(viewLifecycleOwner) { binding.tvLeave.text = it.toString() }

        vm.weeklyStats.observe(viewLifecycleOwner) { stats ->
            if (stats.isEmpty()) return@observe
            val entries = stats.mapIndexed { i, d -> BarEntry(i.toFloat(), d.count.toFloat()) }
            val labels = stats.map { it.date.takeLast(5) } // MM-dd

            val dataSet = BarDataSet(entries, "Daily Present").apply {
                color = Color.parseColor("#6C63FF")
                valueTextColor = Color.WHITE
                valueTextSize = 10f
            }
            binding.barChart.apply {
                data = BarData(dataSet).apply { barWidth = 0.7f }
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    valueFormatter = IndexAxisValueFormatter(labels)
                    textColor = Color.WHITE
                }
                axisLeft.textColor = Color.WHITE
                axisRight.isEnabled = false
                legend.textColor = Color.WHITE
                description.isEnabled = false
                setFitBars(true)
                animateY(800)
                invalidate()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
