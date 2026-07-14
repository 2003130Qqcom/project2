package org.example.config;

import com.github.pagehelper.PageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * MyBatis 插件配置。
 *
 * <p>注册 PageHelper 分页拦截器：
 * 在调用 PageHelper.startPage() 后，MyBatis 查询自动追加 LIMIT 子句。</p>
 */
@Configuration
public class MyBatisConfig {

    /**
     * PageHelper 分页插件 Bean
     *
     * <p>helperDialect=mysql   → 使用 MySQL 分页语法（LIMIT offset, count）
     * reasonable=true         → 分页参数自动修正（pageNum<1 修正为1，超出总页数修正为末页）</p>
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor interceptor = new PageInterceptor();
        Properties props = new Properties();
        props.setProperty("helperDialect", "mysql");
        props.setProperty("reasonable", "true");
        interceptor.setProperties(props);
        return interceptor;
    }
}
