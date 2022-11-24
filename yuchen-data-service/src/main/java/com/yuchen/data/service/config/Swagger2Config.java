package com.yuchen.data.service.config;

import com.google.common.base.Predicate;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * swagger配置
 * @description
 * @author: mazhenwei
 * @date: 2022年2月23日
 */
@Configuration
@ConditionalOnExpression("${swagger.show}")
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket openApi() {
        Predicate<RequestHandler> predicate = new Predicate<RequestHandler>() {
            @Override
            public boolean apply(RequestHandler input) {
                if (input.isAnnotatedWith(ApiOperation.class)) {
                    // 只有添加了ApiOperation注解的method才在API中显示
                    return true;
                }
                return false;
            }
        };
        return new Docket(DocumentationType.SWAGGER_2).groupName("openApi")
                .genericModelSubstitutes(DeferredResult.class).useDefaultResponseMessages(false).forCodeGeneration(false)
                .select().apis(predicate).paths(PathSelectors.any()).build(). globalOperationParameters(globalOperation())
                .apiInfo(openApiInfo());
    }

    private ApiInfo openApiInfo() {
        return new ApiInfoBuilder().title("事件分析系统接口文档").description("事件分析系统接口API").version("0.5.1")
                .contact(new Contact("产品研发部", null, null)).build();
    }
    private List<Parameter> globalOperation() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        //第一个token为传参  的key  ,第二个token  为swagger  页面要显示 的zhi
        tokenPar.name("token").description("token").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        return pars;
    }
}