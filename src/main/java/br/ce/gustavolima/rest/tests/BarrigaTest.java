package br.ce.gustavolima.rest.tests;

import br.ce.gustavolima.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class BarrigaTest extends BaseTest {

    private String TOKEN;

    @Before
    public void login(){
        Map <String, String> login = new HashMap <>();
        login.put("email", "gustavo.lima2@live.com");
        login.put("senha", "123456");

        TOKEN = given()
                .body(login)
                .when()
                .post("/login")
                .then()
                .statusCode(201)
                .extract().path("token")
                ;
    }

    @Test
    public void naoDeveAcessarAPISemToken(){
        given()
                .when()
                    .get("/contas")
                .then()
                    .statusCode(200)
        ;
    }

    @Test
    public void deveIncluirContaComSucesso(){

        given()
                .header("Authorization", "JWT " + TOKEN)// apis mais recentes em vez de jwt é bearer
                .body("{\"nome\": \"conta qualquer\"}")
                .when()
                .post("/contas")
                .then()
                    .statusCode(201)
                ;
    }

    @Test
    public void deveAlterarContaComSucesso(){

        given()
                .header("Authorization", "JWT " + TOKEN)// apis mais recentes em vez de jwt é bearer
                .body("{\"nome\": \"conta alterada\"}")
                .when()
                .put("/contas/68486")
                .then()
                .log().all()
                .statusCode(200)
                .body("nome", is("conta alterada"))
        ;
    }

    @Test
    public void naoDeveInserirMesmoNome(){

        given()
                .header("Authorization", "JWT " + TOKEN)// apis mais recentes em vez de jwt é bearer
                .body("{\"nome\": \"conta alterada\"}")
                .when()
                .post("/contas")
                .then()
                .log().all()
                .statusCode(400)
                .body("error", is("Ja existe uma conta com esse nome!"))
        ;
    }

    @Test
    public void deveInserirMovimentacaoSucesso(){
        Movimentacao mov = getMovimentacaoValida();

        given()
                .header("Authorization", "JWT " + TOKEN)// apis mais recentes em vez de jwt é bearer
                .body(mov)
                .when()
                .post("/transacoes")
                .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveValidarCamposObrigatoriosMovimentacao(){

        given()
                .header("Authorization", "JWT " + TOKEN)// apis mais recentes em vez de jwt é bearer
                .body("{}")
                .when()
                .post("/transacoes")
                .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da movimentacao e obrigatoria",
                        "Data do pagamento e obrigatorio"


                ))
        ;
    }

    @Test
    public void naoDeveInserirMovimentacaoComDataFutura(){
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao("20/05/2021");

        given()
                .header("Authorization", "JWT " + TOKEN)// apis mais recentes em vez de jwt é bearer
                .body(mov)
                .when()
                .post("/transacoes")
                .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da movimentacão deve ser menor ou igual a data atual"))
        ;
    }

    @Test
    public void naoDeveRemoverContaMovimentacao(){

        given()
                .header("Authorization", "JWT " + TOKEN)// apis mais recentes em vez de jwt é bearer
                .when()
                .delete("/contas/68486")
                .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"))
        ;
    }

    @Test
    public void deveCalcularSaldoContas(){

        given()
                .header("Authorization", "JWT " + TOKEN)// apis mais recentes em vez de jwt é bearer
                .when()
                .get("/saldo")
                .then()
                .statusCode(200)
                .body("find{it.conta.id == 68486}.saldo", is("100.00"))
        ;
    }

    @Test
    public void deveRemoverMovimentacao(){

        given()
                .header("Authorization", "JWT " + TOKEN)// apis mais recentes em vez de jwt é bearer
                .when()
                .delete("/transacoes/11588")
                .then()
                .statusCode(204)
                .body("find{it.conta.id == 68486}.saldo", is("100.00"))
        ;
    }

    private Movimentacao getMovimentacaoValida(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(68486);
        //mov.setUsuario_id();
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("Envolvido na movimentacao");
        mov.setTipo("REC");
        mov.setData_transacao("01/01/2000");
        mov.setData_pagamento("10/05/2010");
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;

    }
}



