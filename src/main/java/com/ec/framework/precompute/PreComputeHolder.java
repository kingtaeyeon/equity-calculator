package com.ec.framework.precompute;

import javafx.scene.effect.Reflection;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Sets;
import org.reflections.Reflections;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * PreCompute实现类的类加载器，会扫描calculator-core文件中的precompute.path属性
 *
 * @ClassName PreComputeHolder
 * @Description
 * @Author: LiHao
 * @Since 2023/12/18 17:14
 */
@SuppressWarnings("all")
public class PreComputeHolder {

    public static Set<PreCompute> COMPUTES = Sets.newHashSet();

    private final static String PATH = "precompute.path";

    static {
        Properties properties = new Properties();
        try {
            properties = PropertiesLoaderUtils.loadProperties(new FileSystemResource(Objects.requireNonNull(PreComputeHolder.class.getClassLoader().getResource("calculator-core.properties")).getPath()));
        } catch (IOException ignore) {
        }
        String path = properties.getProperty(PATH);
        if (StringUtils.isNotBlank(path)) {
            Reflections reflections = new Reflections(path);
            Set<Class<? extends PreCompute>> subTypes = reflections.getSubTypesOf(PreCompute.class);
            for (Class<? extends PreCompute> clazz: subTypes) {
                try {
                    COMPUTES.add(clazz.newInstance());
                } catch (Exception ignore) {
                }
            }
        }
    }


}
