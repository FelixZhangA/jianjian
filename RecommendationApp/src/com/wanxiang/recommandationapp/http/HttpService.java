package com.wanxiang.recommandationapp.http;


import com.wanxiang.recommandationapp.controller.FusionService;
import com.wanxiang.recommandationapp.controller.annotation.Actor;
import com.wanxiang.recommandationapp.controller.annotation.Service;

@Service(actorList = {
		@Actor(name = "defaultNetTaskActor", value = DefaultNetTaskActor.class) })
public class HttpService extends FusionService {

}
