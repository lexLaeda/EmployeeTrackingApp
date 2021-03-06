package com.tracking.repository.employee;

import com.tracking.model.employee.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public EmployeeRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Employee save(Employee employee) {
        Session currentSession = sessionFactory.getCurrentSession();
        Long id = (Long) currentSession.save(employee);
        return currentSession.get(Employee.class, id);
    }

    @Override
    public void update(Employee employee) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.saveOrUpdate(employee);
    }

    @Override
    public Employee findById(Long id) {
        Session currentSession = sessionFactory.getCurrentSession();
        Employee employee = currentSession.get(Employee.class, id);
        return employee;
    }

    @Override
    public List<Employee> findAll() {
        Session currentSession = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.select(root);
        Query<Employee> query = currentSession.createQuery(cq);
        return query.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Session currentSession = sessionFactory.getCurrentSession();
        Employee employee = currentSession.byId(Employee.class).load(id);
        currentSession.delete(employee);
    }


}
