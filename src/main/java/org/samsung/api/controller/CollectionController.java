package org.samsung.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.samsung.api.dto.Collection;
import org.samsung.api.dto.CollectionRequest;
import org.samsung.api.dto.Response;
import org.samsung.api.repository.CollectionRedisRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/prodcollector")
@CrossOrigin("*") // TODO : need to change allow only "samsung.com"
public class CollectionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //https://{TBD}/prodrec/collection

    //TODO : apply code formatter

    @Autowired
    CollectionRedisRepository collectionRedisRepository;

    @RequestMapping(method = RequestMethod.PUT, path = "/ ")
    @ResponseBody
    public ResponseEntity<Response> putCollection(HttpServletRequest request,
                                                  @RequestHeader("country") String country,
                                                  @RequestHeader("x-date") String date,
                                                  @RequestHeader("Authorization") String authorization,
                                                  @RequestHeader("x-app-id") String appId,
                                                  @RequestHeader("x-digest") String xDigest,
                                                  @RequestBody String bodyString) throws JsonProcessingException {

        // TODO : check Authorization


        // make response field
        Response response = new Response(HttpStatus.OK.value(), "Success");

        // request body mapping
        CollectionRequest collectionRequest=(new ObjectMapper()).readValue(bodyString, CollectionRequest.class);
        // TODO :check mandatory field (header/body)
        // header : country, x-date, Authorization, x-app-id, x-digest
        // body : event, pageType, modelCode

        // save some fields to redis for real-tiem recommendation
        Collection collection = Collection.builder()
                .uniqueID(UUID.randomUUID().toString()) // FIXME :: generate/save uniqueKey to Collection
                .country(country)
                .x_date(date)
                .guid(collectionRequest.getGuid())
                .visitorId(collectionRequest.getVisitorId())
                .event(collectionRequest.getEvent())
                .pageType(collectionRequest.getPageType())
                .modelCode(collectionRequest.getModelCode())
                .consent(collectionRequest.getConsent())
                .build();

        collectionRedisRepository.save(collection);

        // print debugging log
        System.out.println(collectionRequest);
        logger.error(collectionRequest.toString());
        System.out.println(collection);
        logger.error(collection.toString());

        return new ResponseEntity<Response>(response, HttpStatus.OK);
    }


    // for test
    @RequestMapping(method = RequestMethod.GET, path = "/get")
    @ResponseBody
    public ResponseEntity<List<Collection>> getCollection(HttpServletRequest request,
                                                  @RequestHeader("guid") String guid,
                                                  @RequestBody String bodyString) throws JsonProcessingException {


        //List<Collection> collection = collectionRedisRepository.findByGuid(guid).stream().toList();
        List<Collection> collection = collectionRedisRepository.findByVisitorId(guid).stream().toList();
        logger.error(collection.toString());
        System.out.println(collection);


        return new ResponseEntity<List<Collection>>(collection, HttpStatus.OK);
    }

}
