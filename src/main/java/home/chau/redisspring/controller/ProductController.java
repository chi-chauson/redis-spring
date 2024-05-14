package home.chau.redisspring.controller;

import home.chau.redisspring.dto.ProductDto;
import home.chau.redisspring.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("{id}")
    public Mono<ProductDto> getProduct(@PathVariable Integer id) {
        return this.productService.getProduct(id);
    }

    @PatchMapping("{id}")
    public Mono<Void> updateProduct(@PathVariable Integer id, @RequestBody Mono<ProductDto> mono) {
        return this.productService.updateProduct(id, mono);
    }

    @PostMapping("/by-ids")
    public Flux<ProductDto> getProducts(@RequestBody List<Integer> requestedProductIds) {
        return this.productService.getProducts(Flux.fromIterable(requestedProductIds));
    }

}
