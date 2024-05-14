package home.chau.redisspring.service.impl;

import home.chau.redisspring.dto.ProductDto;
import home.chau.redisspring.repository.ProductRepository;
import home.chau.redisspring.service.ProductService;
import home.chau.redisspring.util.EntityDtoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

@Service
@ConditionalOnProperty(name = "cache.enabled", havingValue = "false")
public class ProductServiceWithNoCache implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceWithNoCache(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Mono<ProductDto> getProduct(Integer id) {
        return this.productRepository.findById(id)
                .map(EntityDtoUtil::toDto);
    }

    @Override
    public Flux<ProductDto> getProducts(Flux<Integer> requestedProductIds) {
        return this.productRepository.findProductsByIds(requestedProductIds)
                .map(EntityDtoUtil::toDto);
    }

    @Override
    public Mono<Void> updateProduct(Integer id, Mono<ProductDto> mono) {
        return this.productRepository.findById(id)
                .zipWith(mono)
                .doOnNext(t -> t.getT1().setQtyAvailable(t.getT2().getQuantityAvailable()))
                .map(Tuple2::getT1)
                .flatMap(this.productRepository::save)
                .then();
    }

}
