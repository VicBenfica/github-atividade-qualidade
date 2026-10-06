package com.exemplo.tarefas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

public class TarefasApiApplicationTest {
    @Test
    @DisplayName("Deve executar o metodo main sem lancar exececoes")
    void mainTest(){
        assertThatCode( () -> TarefasApiApplication.main(new String[]{
                "--spring.main.web-application-type=none"
        })).doesNotThrowAnyException();
    }
}
