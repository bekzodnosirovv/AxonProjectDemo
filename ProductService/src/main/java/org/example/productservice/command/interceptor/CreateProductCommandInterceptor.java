package org.example.productservice.command.interceptor;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.example.productservice.command.CreateProductCommand;
import org.example.productservice.core.data.ProductLookupEntity;
import org.example.productservice.core.data.ProductLookupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {
    private final ProductLookupRepository productLookupRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {

            if (CreateProductCommand.class.equals(command.getPayloadType())) {
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();

                ProductLookupEntity productLookupEntity = productLookupRepository
                        .findByProductIdOrTitle(createProductCommand.getProductId(),
                                createProductCommand.getTitle());

                if (productLookupEntity != null) {

                    LOGGER.error("Product with productId {} or title {} already exist",
                            createProductCommand.getProductId(), createProductCommand.getTitle());

                    throw new IllegalArgumentException(
                            String.format("Product with productId %s or title %s already exist",
                                    createProductCommand.getProductId(), createProductCommand.getTitle())
                    );
                }
            }
            return command;

        };
    }
}
