package team6.car.device.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;
import team6.car.device.domain.NearDevice;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class NearDeviceRepositoryImpl implements NearDeviceRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public NearDevice save(NearDevice near_device){
        em.persist(near_device);
        return near_device;
    }
    public List<NearDevice> findAll() {
        String query = "SELECT nd FROM Near_Device_info nd";
        TypedQuery<NearDevice> typedQuery = em.createQuery(query, NearDevice.class);
        return typedQuery.getResultList();
    }

    public Optional<NearDevice> findByNearDeviceId(String device_id){
        TypedQuery<NearDevice> query = em.createQuery("SELECT nd FROM NearDevice nd WHERE nd.device.device_id = :device_id", NearDevice.class);
        query.setParameter("device_id", device_id);
        return query.getResultStream().findFirst();
    }

    @Override
    public Optional<NearDevice> findById(String id) {
        return Optional.ofNullable(em.find(NearDevice.class, id));
    }

    @Override
    public <S extends NearDevice, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public Optional<NearDevice> findByDeviceId(String device_id) {
        String query = "SELECT nd FROM Near_Device_info nd WHERE nd.device_id = :device_id";
        TypedQuery<NearDevice> typedQuery = em.createQuery(query, NearDevice.class);
        typedQuery.setParameter("device_id", device_id);
        List<NearDevice> nearDevices = typedQuery.getResultList();
        if (nearDevices.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(nearDevices.get(0));
        }
    }

    @Override
    public <S extends NearDevice> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends NearDevice> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends NearDevice> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends NearDevice> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public List<NearDevice> findAll(Sort sort) {
        return null;
    }

    @Override
    public List<NearDevice> findAllById(Iterable<String> Strings) {
        return null;
    }

    @Override
    public Page<NearDevice> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public void flush(){

    }

    @Override
    public <S extends NearDevice> S saveAndFlush(S entity){
        return null;
    }

    @Override
    public <S extends NearDevice> List<S> saveAllAndFlush(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        if (entities == null) {
            return result;
        }
        for (S entity : entities) {
            result.add(saveAndFlush(entity));
        }
        flush();
        return result;
    }

    @Override
    public void deleteAllInBatch(Iterable<NearDevice> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> Strings) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public NearDevice getOne(String aString) {
        return null;
    }

    @Override
    public NearDevice getById(String aString) {
        return null;
    }

    @Override
    public NearDevice getReferenceById(String aString) {
        return null;
    }

    @Override
    public <S extends NearDevice> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends NearDevice> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public boolean existsById(String aString) {
        return false;
    }

    @Override
    public <S extends NearDevice> List<S> saveAll(Iterable<S> entities) {
        return null;
    }
    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String aString) {

    }

    @Override
    public void delete(NearDevice entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends NearDevice> entities) {

    }

    @Override
    public void deleteAll() {

    }

}
