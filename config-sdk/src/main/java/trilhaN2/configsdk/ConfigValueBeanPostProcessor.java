package trilhaN2.configsdk;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigValueBeanPostProcessor implements BeanPostProcessor, ApplicationListener<ConfigUpdateEvent> {

    private final ConfigClient client;
    private final String ns, env;
    private final Map<String, List<FieldBinding>> bindings = new ConcurrentHashMap<>();

    record FieldBinding(Object bean, Field field){}

    public ConfigValueBeanPostProcessor(ConfigClient client, ConfigSdkProperties props) {
        this.client = client; this.ns = props.getNamespace(); this.env = props.getEnv();
    }

    private String fk(String key){ return ns + ":" + env + ":" + key; }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), f -> {
            var ann = f.getAnnotation(ConfigValue.class);
            if (ann != null) {
                String key = ann.value();
                f.setAccessible(true);
                String v = client.get(key); // valor inicial via cache/fallback
                if (v != null) setField(f, bean, v);
                bindings.computeIfAbsent(fk(key), k -> new ArrayList<>()).add(new FieldBinding(bean, f));
            }
        });
        return bean;
    }

    @Override
    public void onApplicationEvent(ConfigUpdateEvent ev) {
        if (!ns.equals(ev.namespace) || !env.equals(ev.env)) return;
        String qual = fk(ev.key);
        for (FieldBinding fb : bindings.getOrDefault(qual, List.of())) {
            var f = fb.field();
            ReflectionUtils.makeAccessible(f);
            try {
                if (ev.type == ConfigUpdateEvent.Type.DELETE) {
                    if (!f.getType().isPrimitive()) f.set(fb.bean(), null);
                } else {
                    setField(f, fb.bean(), ev.value);
                }
            } catch (Exception ignore) { /* log se quiser */ }
        }
    }

    private void setField(Field f, Object bean, String v) throws IllegalAccessException {
        if (f.getType() == String.class) f.set(bean, v);
        else if (f.getType() == int.class || f.getType() == Integer.class) f.set(bean, Integer.parseInt(v));
        else if (f.getType() == boolean.class || f.getType() == Boolean.class) f.set(bean, Boolean.parseBoolean(v));
        else f.set(bean, v);
    }
}
