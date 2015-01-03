package com.wanxiang.recommandationapp.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wanxiang.recommandationapp.controller.FusionService;

@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME) 
public @interface Service {
	
	String name() default "";
	Class<? extends FusionService> value() default FusionService.class;
	Actor[] actorList() default{};
	
}
