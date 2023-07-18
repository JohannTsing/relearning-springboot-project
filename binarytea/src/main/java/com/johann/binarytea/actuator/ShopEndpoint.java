package com.johann.binarytea.actuator;

import com.johann.binarytea.BinaryTeaProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

/**
 * 内置端点不满足，开发自己的端点
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Component
@Endpoint(id = "shopEndpoint")
public class ShopEndpoint {

    private BinaryTeaProperties binaryTeaProperties;

    public ShopEndpoint(ObjectProvider<BinaryTeaProperties> binaryTeaProperties) {
        this.binaryTeaProperties = binaryTeaProperties.getIfAvailable();
    }

    @ReadOperation
    public String state(){
        if (binaryTeaProperties==null || !binaryTeaProperties.isReady()) {
            return "Shop is not ready!";
        } else {
            return "OpenHours: "+binaryTeaProperties.getOpenHours()+"\n"+
                    "Shop is ready!";
        }
    }
}
