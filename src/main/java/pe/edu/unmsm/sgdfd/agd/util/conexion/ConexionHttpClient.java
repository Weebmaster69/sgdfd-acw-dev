/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util.conexion;

import java.io.IOException;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pe.edu.unmsm.sgdfd.agd.to.DataGeneracionMasivaTO;
import pe.edu.unmsm.sgdfd.agd.to.DocumentoDummyTO;
import pe.edu.unmsm.sgdfd.agd.to.SolicitudGuardarDocumentoTO;
import pe.edu.unmsm.sgdfd.agd.to.SolicitudMasivaDocumentoTO;

/**
 *
 * @author antony.almonacid
 */
public class ConexionHttpClient {
    
    static Logger LOG = LogManager.getLogger(ConexionHttpClient.class.getName());
    
    private static HttpComponentsClientHttpRequestFactory hcchrf = new HttpComponentsClientHttpRequestFactory();
    protected static RestTemplate restTemplate = new RestTemplate(hcchrf);
    protected static RestTemplate restTemplateSinTimeOut = new RestTemplate();

    protected static String url;
    private static String MENSAJE_LOG = "Error al realizar consulta " + url;
    protected static final String MENSAJE = "Error conectar al módulo generador de documentos";
    protected static final String URL_MCC = "http://localhost:8081/sgdfd/mcc/backend";
    // protected static final String URL_MCC = "http://desarrollocp.unmsm.edu.pe:8080/sgdfd/mcc/backend";
    // protected static final String URL_MCC = "http://172.16.156.169:8080/sgdfd/mcc/backend";

    public static void inicializar() {
        hcchrf.setConnectionRequestTimeout(60000); // Este es el tiempo de espera en milis para obtener la conexión de connectionManager 6000
        hcchrf.setConnectTimeout(70000); // Este es el tiempo de espera en milisegundos para establecer la conexión entre el origen y el destino 7000
        hcchrf.setReadTimeout(100000);// Este es el tiempo de espera en milisegundos para establecer la conexión entre el origen y el destino 10000
    }
    
    public DataGeneracionMasivaTO recopilarData(SolicitudMasivaDocumentoTO solicitud, WebSocket websocket){
       
         try {
             
            HttpHeaders headers = new HttpHeaders();
            // set `content-type` header
            headers.setContentType(MediaType.APPLICATION_JSON);
		
            // set `accept` header
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.add("Authorization", "Bearer " + solicitud.getToken());
            // create request
            HttpEntity<?> request = new HttpEntity<Object>(solicitud,headers);

            DataGeneracionMasivaTO response = restTemplate.postForObject(URL_MCC + "/documentos/preprocesar/generacion", request, DataGeneracionMasivaTO.class);
            return response;
        } catch (HttpServerErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            // Parse JSON response body to extract the message
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode;
            String errorMessage = null;
            try {
                jsonNode = objectMapper.readTree(responseBody);
                errorMessage = jsonNode.get("message").asText();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            

            LOG.fatal("Error en recopilar data: {}", errorMessage , e);
            websocket.send("Error: " + errorMessage);
            throw new RuntimeException("Error en recopilar data: " + errorMessage , e);
        } catch (ResourceAccessException | HttpClientErrorException e) {
            LOG.fatal(MENSAJE_LOG, e);

            websocket.send("Error: " + "No se pudo recopilar la información necesaria para la generación de los documentos, por favor consulte con su administrador de base de datos.");
            throw new RuntimeException("Error en recopilar data", e);
        }
         
    }
    
    public void guardarDocumentos(SolicitudGuardarDocumentoTO solicitud, WebSocket websocket){
        try {
            
            HttpHeaders headers = new HttpHeaders();
            // set `content-type` header
            headers.setContentType(MediaType.APPLICATION_JSON);
		
            // set `accept` header
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.add("Authorization", "Bearer " + solicitud.getToken());
            // create request
            HttpEntity<?> request = new HttpEntity<Object>(solicitud,headers);
                
            restTemplate.postForObject(URL_MCC + "/documentos/guardar", request, SolicitudGuardarDocumentoTO.class);
        
        } catch (HttpServerErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            // Parse JSON response body to extract the message
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode;
            String errorMessage = null;
            try {
                jsonNode = objectMapper.readTree(responseBody);
                errorMessage = jsonNode.get("message").asText();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            

            LOG.fatal("Error en guardar documentos: {}", errorMessage , e);
            websocket.send("Error: " + errorMessage);
            throw new RuntimeException("Error en guardar documentos: " + errorMessage , e);   
        } catch (ResourceAccessException | HttpClientErrorException e) {
            LOG.fatal(MENSAJE_LOG, e);
            //websocket.send("Error: "+ e.getMessage());
            websocket.send("Error: "+ e);
            throw new RuntimeException("Error en guardar documentos" + "\n");
        }
    }
    
    public DataGeneracionMasivaTO recopilarDataDummy(SolicitudMasivaDocumentoTO solicitud, WebSocket websocket){
       
         try {
             
            HttpHeaders headers = new HttpHeaders();
            // set `content-type` header
            headers.setContentType(MediaType.APPLICATION_JSON);
		
            // set `accept` header
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.add("Authorization", "Bearer " + solicitud.getToken());
            // create request
            HttpEntity<?> request = new HttpEntity<Object>(solicitud,headers);
                
            DataGeneracionMasivaTO response = restTemplate.postForObject(URL_MCC + "/documentos-generacion/preprocesar/dummy", request, DataGeneracionMasivaTO.class);
            
            return response;

        } catch (HttpServerErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            // Parse JSON response body to extract the message
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode;
            String errorMessage = null;
            try {
                jsonNode = objectMapper.readTree(responseBody);
                errorMessage = jsonNode.get("message").asText();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            

            LOG.fatal("Error en recopilar data dummy: {}", errorMessage , e);
            websocket.send("Error: " + errorMessage);
            throw new RuntimeException("Error en recopilar data dummy: " + errorMessage , e);  
        } catch (ResourceAccessException | HttpClientErrorException e) {
            LOG.fatal(MENSAJE_LOG, e);

            websocket.send("Error: "+ e);
            throw new RuntimeException("error recopilar data dummy" + "\n");
        }
         
    }
    
    
    public DocumentoDummyTO guardarDocumentosDummy(DocumentoDummyTO solicitud, WebSocket websocket){
        try {
            
            HttpHeaders headers = new HttpHeaders();
            // set `content-type` header
            headers.setContentType(MediaType.APPLICATION_JSON);
		
            // set `accept` header
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.add("Authorization", "Bearer " + solicitud.getToken());
            // create request
            HttpEntity<?> request = new HttpEntity<Object>(solicitud,headers);
                
            DocumentoDummyTO response = restTemplate.postForObject(URL_MCC + "/documentos-generacion/guardar/dummy", request, DocumentoDummyTO.class);
            
            return response;

        } catch (HttpServerErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            // Parse JSON response body to extract the message
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode;
            String errorMessage = null;
            try {
                jsonNode = objectMapper.readTree(responseBody);
                errorMessage = jsonNode.get("message").asText();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            

            LOG.fatal("Error en guardar documentos dummy: {}", errorMessage , e);
            websocket.send("Error: " + errorMessage);
            throw new RuntimeException("Error en guardar documentos dummy: " + errorMessage , e);  
        } catch (ResourceAccessException | HttpClientErrorException e) {
            LOG.fatal(MENSAJE_LOG, e);

            websocket.send("Error: "+ e);
            throw new RuntimeException("Error en guardar documentos dummy" + "\n");
        }
    }
    
}
