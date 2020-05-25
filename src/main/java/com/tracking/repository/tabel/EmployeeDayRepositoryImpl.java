package com.tracking.repository.tabel;

import com.tracking.model.employee.Employee;
import com.tracking.model.tabel.EmployeeDay;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class EmployeeDayRepositoryImpl implements EmployeeDayRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public EmployeeDayRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Set<EmployeeDay> getEmployeeDaysByEmployee(Employee employee) {
        Session currentSession = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaQuery<EmployeeDay> cq = cb.createQuery(EmployeeDay.class);
        Root<EmployeeDay> root = cq.from(EmployeeDay.class);
        cq.select(root).where(cb.equal(root.get("employee"), employee));
        Query<EmployeeDay> query = currentSession.createQuery(cq);
        List<EmployeeDay> resultList = query.getResultList();
        return new HashSet<>(resultList);
    }

    @Override
    public void saveEmployeeDay(EmployeeDay employeeDay) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.saveOrUpdate(employeeDay);
    }
}
