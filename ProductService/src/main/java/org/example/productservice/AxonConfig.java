package org.example.productservice;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AxonConfig {

    @Bean
    @Primary
    public Serializer serializer() {
        XStream xStream = new XStream();
        xStream.addPermission(AnyTypePermission.ANY);   // Barcha turlarga ruxsat berish
        return XStreamSerializer.builder().xStream(xStream).build();
    }
}