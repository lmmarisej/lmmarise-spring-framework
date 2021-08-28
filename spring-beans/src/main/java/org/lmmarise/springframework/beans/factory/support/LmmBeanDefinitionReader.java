package org.lmmarise.springframework.beans.factory.support;

import org.lmmarise.springframework.beans.factory.config.LmmBeanDefinition;
import org.lmmarise.springframework.util.ClassUtils;
import org.lmmarise.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * 对配置文件进行查找、读取、解析
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 6:21 下午
 */
public class LmmBeanDefinitionReader {

    private final List<String> registryBeanClasses = new ArrayList<>();
    private final Properties config = new Properties();
    // 规范，固定配置文件中的key为'scanPackage'
    private final String SCAN_PACKAGE = "scanPackage";
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    public LmmBeanDefinitionReader(String... locations) {
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""))) {
            this.config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    /**
     * 扫描配置文件中 scanPackage 属性指定的包下所有的字节码文件，获得该指定目录下所有的全限类名并放入
     * {@link LmmBeanDefinitionReader#registryBeanClasses}
     */
    private void doScanner(String scanPackage) {
        // 以“/”开头表示的是src根目录下开始查找。如果不是以“/”开头的则表示从当前类的包中开始查找。
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        if (url == null) {
            throw new RuntimeException("Package '" + scanPackage +  "' not found.");
        }
        File classPath = new File(url.getFile());
        for (File file : Objects.requireNonNull(classPath.listFiles())) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class", "").replaceAll("/", "."));
                this.registryBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig() {
        return this.config;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = (beanClassLoader != null ? beanClassLoader : ClassUtils.getDefaultClassLoader());
    }

    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }


    public List<LmmBeanDefinition> loadBeanDefinitions() {
        ArrayList<LmmBeanDefinition> result = new ArrayList<>();
        try {
            for (String className : this.registryBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) {
                    continue;
                }
                result.add(doCreateBeanDefinition(StringUtils.toLowerCaseFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    result.add(doCreateBeanDefinition(i.getName(), i.getName()));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private LmmBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        LmmBeanDefinition beanDefinition = new LmmBeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }

}
