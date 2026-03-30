package com.vatsalya.staffattendance.data.db

import androidx.room.TypeConverter
import com.vatsalya.staffattendance.data.entity.AttendanceStatus
import com.vatsalya.staffattendance.data.entity.EmployeeRole
import com.vatsalya.staffattendance.data.entity.LeaveStatus
import com.vatsalya.staffattendance.data.entity.LeaveType
import com.vatsalya.staffattendance.data.entity.Shift

class Converters {
    @TypeConverter fun shiftToStr(s: Shift): String = s.name
    @TypeConverter fun strToShift(s: String): Shift = Shift.valueOf(s)

    @TypeConverter fun roleToStr(r: EmployeeRole): String = r.name
    @TypeConverter fun strToRole(s: String): EmployeeRole = EmployeeRole.valueOf(s)

    @TypeConverter fun attStatusToStr(a: AttendanceStatus): String = a.name
    @TypeConverter fun strToAttStatus(s: String): AttendanceStatus = AttendanceStatus.valueOf(s)

    @TypeConverter fun leaveTypeToStr(l: LeaveType): String = l.name
    @TypeConverter fun strToLeaveType(s: String): LeaveType = LeaveType.valueOf(s)

    @TypeConverter fun leaveStatusToStr(l: LeaveStatus): String = l.name
    @TypeConverter fun strToLeaveStatus(s: String): LeaveStatus = LeaveStatus.valueOf(s)
}
