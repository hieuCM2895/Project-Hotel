package dao;

import dao.interfaces.IClientDAO;
import model.Booking;
import model.Client;
import org.hibernate.Session;
import util.HibernateUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class ClientDAOImpl extends AbstractDAO<Client, Object> implements IClientDAO {

    @Override
    public List<Object[]> findProfitFromCustomers() {

        // select sum(b.total_amount) from booking b group by b.client_id
        Session session = HibernateUtils.getSessionFactory().openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = builder.createQuery(Object[].class);

        Root<Booking> root = criteriaQuery.from(Booking.class);

        criteriaQuery.multiselect(root.get("client").get("clientId"),
                builder.sum(root.get("totalAmount")).alias("Total Amount"), builder.count(root.get("bookingDate")).alias("Total count booked"));
        criteriaQuery.groupBy(root.get("client").get("clientId")).orderBy(builder.desc(builder.sum(root.get("totalAmount"))));

        return session.createQuery(criteriaQuery).getResultList();

    }

    @Override
    public Client findClientByName(String clientName) {

        Session session = HibernateUtils.getSessionFactory().openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Client> criteriaQuery = builder.createQuery(Client.class);

        Root<Client> root = criteriaQuery.from(Client.class);

        criteriaQuery.select(root).where(builder.like(root.get("fullName"), clientName));

        return session.createQuery(criteriaQuery).getSingleResult();
    }

}
