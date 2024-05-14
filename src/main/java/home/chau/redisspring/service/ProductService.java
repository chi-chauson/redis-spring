package home.chau.redisspring.service;

import home.chau.redisspring.dto.ProductDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {

    Mono<ProductDto> getProduct(Integer id);
    Flux<ProductDto> getProducts(Flux<Integer> requestedProductIds);
    Mono<Void> updateProduct(Integer id, Mono<ProductDto> productDto);

}
