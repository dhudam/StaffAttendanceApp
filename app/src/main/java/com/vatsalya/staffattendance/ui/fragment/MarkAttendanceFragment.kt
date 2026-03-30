package com.vatsalya.staffattendance.ui.fragment

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vatsalya.staffattendance.R
import com.vatsalya.staffattendance.data.entity.Employee
import com.vatsalya.staffattendance.databinding.FragmentMarkAttendanceBinding
import com.vatsalya.staffattendance.util.BiometricHelper
import com.vatsalya.staffattendance.viewmodel.AttendanceViewModel
import java.text.SimpleDateFormat
import java.util.*

class MarkAttendanceFragment : Fragment() {
    private var _binding: FragmentMarkAttendanceBinding? = null
    private val binding get() = _binding!!
    private val vm: AttendanceViewModel by viewModels()

    private var employees = listOf<Employee>()
    private var selectedEmployee: Employee? = null
    private var photoPath: String = ""
    private var lastLat: Double? = null
    private var lastLon: Double? = null

    private lateinit var fusedLocation: FusedLocationProviderClient
    private var imageCapture: ImageCapture? = null
    private var cameraMode = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentMarkAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())

        vm.allEmployees.observe(viewLifecycleOwner) { list ->
            employees = list
            val names = list.map { "${it.name} (${it.employeeCode})" }
            binding.spinnerEmployee.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
        }

        vm.markResult.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        vm.currentRecord.observe(viewLifecycleOwner) { record ->
            if (record != null) {
                binding.tvStatus.text = "Checked IN at ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(record.checkInTime ?: 0L))}"
                binding.btnCheckIn.isEnabled = false
                binding.btnCheckOut.isEnabled = true
            } else {
                binding.tvStatus.text = "Not checked in yet"
                binding.btnCheckIn.isEnabled = true
                binding.btnCheckOut.isEnabled = false
            }
        }

        binding.btnCheckIn.setOnClickListener {
            val emp = getSelectedEmployee() ?: return@setOnClickListener
            getLocation {
                if (BiometricHelper.isBiometricAvailable(requireContext())) {
                    BiometricHelper.authenticate(
                        requireActivity(),
                        onSuccess = { doCheckIn(emp) },
                        onError = { showCameraCapture(emp) },
                        onFail = { Toast.makeText(context, "Fingerprint not recognized", Toast.LENGTH_SHORT).show() }
                    )
                } else {
                    showCameraCapture(emp)
                }
            }
        }

        binding.btnCheckOut.setOnClickListener {
            val emp = getSelectedEmployee() ?: return@setOnClickListener
            vm.checkOut(emp)
        }

        binding.btnCapture.setOnClickListener { capturePhoto() }
        binding.btnCancelCamera.setOnClickListener { hideCameraCapture() }
    }

    private fun getSelectedEmployee(): Employee? {
        val idx = binding.spinnerEmployee.selectedItemPosition
        return if (employees.isNotEmpty() && idx >= 0) {
            employees[idx].also { vm.loadRecord(it.id) }
        } else {
            Toast.makeText(context, "Please select an employee", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun getLocation(onDone: () -> Unit) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocation.lastLocation.addOnSuccessListener { loc: Location? ->
                lastLat = loc?.latitude
                lastLon = loc?.longitude
                onDone()
            }.addOnFailureListener { onDone() }
        } else onDone()
    }

    private fun doCheckIn(emp: Employee) {
        vm.checkIn(emp, photoPath, lastLat, lastLon)
        hideCameraCapture()
    }

    private fun showCameraCapture(emp: Employee) {
        selectedEmployee = emp
        cameraMode = true
        binding.cameraGroup.visibility = View.VISIBLE
        startCamera()
    }

    private fun hideCameraCapture() {
        cameraMode = false
        binding.cameraGroup.visibility = View.GONE
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e("Camera", "Bind failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "ATT_$name.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    photoPath = output.savedUri?.toString() ?: ""
                    selectedEmployee?.let { doCheckIn(it) }
                }
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(context, "Photo failed: ${exc.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
