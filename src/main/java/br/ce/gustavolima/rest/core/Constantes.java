package br.ce.gustavolima.rest.core;
import io.restassured.http.*;

public interface Constantes {

    String APP_BASE_URL = "http://seubarriga.wcaquino.me";
    Integer APP_PORT = 80; //para https 443
    String APP_BASE_PATH = "";

    ContentType APP_CONTENT_TYPE = ContentType.JSON;

    Long MAX_TIMEOUT = 5000L;
}
