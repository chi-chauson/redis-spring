package home.chau.redisspring.repository;

import home.chau.redisspring.entity.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {
    @Query("SELECT * FROM product WHERE id IN (:productIds)")
    Flux<Product> findProductsByIds(Flux<Integer> productIds);
}
