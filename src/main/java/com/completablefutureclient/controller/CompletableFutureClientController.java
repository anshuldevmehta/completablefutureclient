package com.completablefutureclient.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class CompletableFutureClientController {

    final String URI_FIRST_SAMPLE="http://localhost:8081/first";
    final String URI_SECOND_SAMPLE="http://localhost:8082/second";
    final String EXEC_MODE_SERIAL="serial";
    final String EXEC_MODE_PARALLEL="parallel";
    @RequestMapping("/future/{execmode}")
    public String getResponseFromClients(@PathVariable String execmode) throws ExecutionException, InterruptedException {

        String response=null;
        switch(execmode)
        {
            case EXEC_MODE_SERIAL:
                response=makeSerialCalls();
                break;
            case EXEC_MODE_PARALLEL:
                response=makeParallelCalls();
                break;
            default:
                response= "Please supply correct execution  mode";

        }
        return response;
    }

    public String makeSerialCalls()
    {
        RestTemplate restTemplate=new RestTemplate();
        String firstServiceResponse=restTemplate.getForObject(URI_FIRST_SAMPLE,String.class);
        String secondServiceResponse=restTemplate.getForObject(URI_SECOND_SAMPLE,String.class);
        return firstServiceResponse+secondServiceResponse;
    }


    private String makeParallelCalls() throws ExecutionException, InterruptedException {
        RestTemplate restTemplate=new RestTemplate();
        CompletableFuture service1Future=CompletableFuture.supplyAsync(() -> {
        String response=restTemplate.getForObject(URI_FIRST_SAMPLE,String.class);
        return response;
            });

        CompletableFuture service2Future=CompletableFuture.supplyAsync(() -> {
            return restTemplate.getForObject(URI_SECOND_SAMPLE,String.class);
        });
        while(!service1Future.isDone() || !service2Future.isDone())
        {
            continue;
        }
        return service1Future.get().toString() + service2Future.get().toString();
    }
}