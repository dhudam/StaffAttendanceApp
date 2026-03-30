package com.vatsalya.staffattendance.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vatsalya.staffattendance.data.entity.Employee
import com.vatsalya.staffattendance.data.entity.EmployeeRole
import com.vatsalya.staffattendance.data.entity.Shift
import com.vatsalya.staffattendance.databinding.FragmentAddEditEmployeeBinding
import com.vatsalya.staffattendance.viewmodel.EmployeeViewModel

class AddEditEmployeeFragment : Fragment() {
    private var _binding: FragmentAddEditEmployeeBinding? = null
    private val binding get() = _binding!!
    private val vm: EmployeeViewModel by viewModels()
    private var existingId: Long = -1L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentAddEditEmployeeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        existingId = arguments?.getLong("employeeId", -1L) ?: -1L

        // Populate shift spinner
        val shifts = Shift.values().map { it.name }
        binding.spinnerShift.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, shifts).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Populate role spinner
        val roles = EmployeeRole.values().map { it.name }
        binding.spinnerRole.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, roles).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.btnSave.setOnClickListener { saveEmployee() }
    }

    private fun saveEmployee() {
        val name = binding.etName.text.toString().trim()
        val code = binding.etCode.text.toString().trim()
        val dept = binding.etDept.text.toString().trim()
        val desig = binding.etDesignation.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (name.isEmpty() || code.isEmpty() || dept.isEmpty()) {
            Toast.makeText(context, "Name, Employee Code, and Department are required", Toast.LENGTH_SHORT).show()
            return
        }

        val shift = Shift.values()[binding.spinnerShift.selectedItemPosition]
        val role = EmployeeRole.values()[binding.spinnerRole.selectedItemPosition]

        val employee = Employee(
            id = if (existingId > 0) existingId else 0,
            name = name,
            employeeCode = code,
            department = dept,
            designation = desig,
            shift = shift,
            role = role,
            phone = phone,
            email = email
        )

        if (existingId > 0) vm.updateEmployee(employee) else vm.addEmployee(employee)
        Toast.makeText(context, if (existingId > 0) "Employee updated" else "Employee added", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
