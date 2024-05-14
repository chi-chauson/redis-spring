package home.chau.redisspring.service.impl;

import home.chau.redisspring.dto.ProductDto;
import home.chau.redisspring.repository.ProductRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class ProductServiceWithRedisCache extends ProductServiceWithNoCache {

    private static final String KEY = "product";

    private final ReactiveHashOperations<String, Integer, ProductDto> hashOperations;

    public ProductServiceWithRedisCache(ProductRepository productRepository, ReactiveHashOperations<String, Integer, ProductDto> hashOperations) {
        super(productRepository);
        this.hashOperations = hashOperations;
    }

    @Override
    public Mono<ProductDto> getProduct(Integer id) {
        return hashOperations.get(KEY, id)
                .switchIfEmpty(this.getProductFromDatabaseAndCache(id));
    }

    @Override
    public Flux<ProductDto> getProducts(Flux<Integer> requestedProductIds) {
        // Filter requested IDs based on cache presence
        Flux<ProductDto> cachedProducts = requestedProductIds.flatMap(id -> hashOperations.get(KEY, id));
        Flux<Integer> requestedMissingIds = requestedProductIds
                .filterWhen(id -> hashOperations.hasKey(KEY, id).map(exists -> !exists));
        Flux<ProductDto> databaseProducts = super.getProducts(requestedMissingIds).flatMap(
                productDto -> hashOperations.put(KEY, productDto.getId(), productDto).thenReturn(productDto));
        return Flux.concat(cachedProducts, databaseProducts);
    }

    @Override
    public Mono<Void> updateProduct(Integer id, Mono<ProductDto> mono) {
        return super.updateProduct(id, mono)
                .then(this.hashOperations.remove(KEY, id))
                .then();
    }

    private Mono<ProductDto> getProductFromDatabaseAndCache(Integer id) {
        return super.getProduct(id)
                .flatMap(dto -> this.hashOperations.put(KEY, id, dto)
                        .thenReturn(dto));
    }

}
