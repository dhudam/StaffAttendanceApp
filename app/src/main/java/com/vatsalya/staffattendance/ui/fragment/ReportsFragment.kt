package com.vatsalya.staffattendance.ui.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.vatsalya.staffattendance.databinding.FragmentReportsBinding
import com.vatsalya.staffattendance.data.db.AppDatabase
import com.vatsalya.staffattendance.data.entity.Employee
import com.vatsalya.staffattendance.util.CsvExporter
import com.vatsalya.staffattendance.util.ShiftUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ReportsFragment : Fragment() {
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private var fromDate = ShiftUtils.daysAgo(7)
    private var toDate = ShiftUtils.todayDate()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvFromDate.text = fromDate
        binding.tvToDate.text = toDate

        binding.btnPickFrom.setOnClickListener { pickDate(true) }
        binding.btnPickTo.setOnClickListener { pickDate(false) }

        binding.btnExport.setOnClickListener { exportCsv() }
    }

    private fun pickDate(isFrom: Boolean) {
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, y, m, d ->
            val date = String.format("%04d-%02d-%02d", y, m + 1, d)
            if (isFrom) { fromDate = date; binding.tvFromDate.text = date }
            else { toDate = date; binding.tvToDate.text = date }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun exportCsv() {
        binding.btnExport.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val records = withContext(Dispatchers.IO) {
                db.attendanceDao().getByDateRangeSync(fromDate, toDate)
            }
            val empMap = withContext(Dispatchers.IO) {
                db.employeeDao().getAllActive().value
                    ?.associateBy { it.id } ?: mapOf()
            }
            val file = withContext(Dispatchers.IO) {
                CsvExporter.exportAttendance(requireContext(), records, empMap)
            }
            binding.btnExport.isEnabled = true
            if (file != null) {
                val uri = FileProvider.getUriForFile(
                    requireContext(), "${requireContext().packageName}.fileprovider", file
                )
                val share = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(share, "Share Attendance Report"))
            } else {
                Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
