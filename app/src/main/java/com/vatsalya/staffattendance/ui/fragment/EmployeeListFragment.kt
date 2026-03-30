package com.vatsalya.staffattendance.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vatsalya.staffattendance.R
import com.vatsalya.staffattendance.databinding.FragmentEmployeeListBinding
import com.vatsalya.staffattendance.ui.adapter.EmployeeAdapter
import com.vatsalya.staffattendance.viewmodel.EmployeeViewModel

class EmployeeListFragment : Fragment() {
    private var _binding: FragmentEmployeeListBinding? = null
    private val binding get() = _binding!!
    private val vm: EmployeeViewModel by viewModels()
    private lateinit var adapter: EmployeeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentEmployeeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EmployeeAdapter { employee ->
            val bundle = Bundle().apply { putLong("employeeId", employee.id) }
            findNavController().navigate(R.id.action_employees_to_addEdit, bundle)
        }
        binding.recyclerEmployees.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEmployees.adapter = adapter

        vm.allEmployees.observe(viewLifecycleOwner) { adapter.submitList(it) }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val q = s.toString().trim()
                if (q.isEmpty()) vm.allEmployees.observe(viewLifecycleOwner) { adapter.submitList(it) }
                else vm.search(q).observe(viewLifecycleOwner) { adapter.submitList(it) }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.fabAddEmployee.setOnClickListener {
            findNavController().navigate(R.id.action_employees_to_addEdit)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
