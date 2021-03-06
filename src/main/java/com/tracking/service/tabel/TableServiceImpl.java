package com.tracking.service.tabel;

import com.tracking.model.employee.Department;
import com.tracking.model.employee.Employee;
import com.tracking.model.tabel.Code;
import com.tracking.model.tabel.DepartmentTable;
import com.tracking.model.tabel.EmployeeDay;
import com.tracking.model.tabel.EmployeeTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TableServiceImpl implements TableService {

    private EmployeeDayService employeeDayService;


    @Autowired
    public TableServiceImpl(EmployeeDayService employeeDayService) {
        this.employeeDayService = employeeDayService;
    }

    @Override
    public DepartmentTable getDepartmentTable(Department department, Set<Employee> employees, int month) {
        List<EmployeeTable> employeeTableSet = employees.stream()
                .map(employee -> getEmployeeTable(employee, getEmployeeDaysByMonth(month, employee)))
                .collect(Collectors.toList());
        DepartmentTable departmentTable = new DepartmentTable();
        departmentTable.setDepartment(department);
        departmentTable.setEmployeeTables(employeeTableSet);
        departmentTable.setMonth(month);
        int length = getMonthLength(month);
        List<Integer> head = getHead(length);
        departmentTable.setDays(head);
        return departmentTable;
    }

    @Override
    public void saveEmployeeTable(Integer month, Employee employee, List<Code> employeeStatusList) {
        for (int i = 0; i < employeeStatusList.size(); i++) {
            EmployeeDay employeeDay = getEmployeeDay(i + 1, employeeStatusList.get(i), employee, month);
            employeeDayService.save(employeeDay);
        }
    }

    @Override
    public EmployeeTable getEmployeeTable(Employee employee, Integer month) {
        EmployeeTable employeeTable = new EmployeeTable();
        List<EmployeeDay> employeeDaysByMonth = getEmployeeDaysByMonth(month, employee);
        employeeTable.setEmployeeDays(employeeDaysByMonth);
        employeeTable.setEmployee(employee);
        employeeTable.setMonth(employeeDaysByMonth.get(0).getLocalDate().getMonth());
        return employeeTable;
    }

    private EmployeeDay getEmployeeDay(int day, Code code, Employee employee, Integer month) {
        LocalDate now = LocalDate.now();
        LocalDate localDate = LocalDate.of(now.getYear(), month, day);
        EmployeeDay employeeDayByEmployeeAndDate = employeeDayService.getEmployeeDayByEmployeeAndDate(employee, localDate);
        employeeDayByEmployeeAndDate.setCode(code);
        return employeeDayByEmployeeAndDate;
    }


    private List<Integer> getHead(int length) {
        List<Integer> head = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            head.add(i);
        }
        return head;
    }

    private int getMonthLength(int month) {
        LocalDate current = LocalDate.now();

        LocalDate date = LocalDate.of(current.getYear(), month, 1);
        return date.getMonth().length(current.isLeapYear());
    }

    private List<EmployeeDay> getEmployeeDaysByMonth(int month, Employee employee) {
        List<EmployeeDay> fromDb = fromDb(month, employee);
        if (fromDb.size() != 0) {
            return fromDb;
        }
        return createNew(month, employee);
    }

    private List<EmployeeDay> createNew(int month, Employee employee) {
        LocalDate current = LocalDate.now();
        int length = getMonthLength(month);
        return Stream.iterate(1, n -> n + 1)
                .limit(length)
                .map(day -> {
                    LocalDate localDate = LocalDate.of(current.getYear(), month, day);
                    EmployeeDay employeeDay = new EmployeeDay(employee, localDate);
                    employeeDayService.save(employeeDay);
                    return employeeDay;
                })
                .collect(Collectors.toList());
    }

    private List<EmployeeDay> fromDb(int month, Employee employee) {
        return employeeDayService.getEmployeeDaysByEmployee(employee).stream()
                .filter(employeeDay -> employeeDay.getLocalDate().getMonth().getValue() == month)
                .collect(Collectors.toList());
    }

    private EmployeeTable getEmployeeTable(Employee employee, List<EmployeeDay> employeeDays) {
        EmployeeTable employeeTable = new EmployeeTable();
        employeeTable.setEmployee(employee);
        employeeTable.setEmployeeDays(employeeDays);
        employeeTable.setMonth(employeeDays.get(0).getLocalDate().getMonth());
        return employeeTable;
    }
}
