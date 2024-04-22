package com.noodles._process;

import com.noodles.util.Constants;
import org.camunda.bpm.scenario.Scenario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;

/**
 * Тестовый класс.
 */
public class SchemeTest extends AbstractProcessTest {

    /**
     * id процесса.
     */
    final String PROCESS_KEY = "CookMasalaVeggiesNoodles";

    /**
     * Объект, в котором хранятся методы взаимодействия.
     */
    private SchemeObject schemeObject;

    /**
     * Инициализация.
     */
    @Override
    protected void init() {
        schemeObject = new SchemeObject(processScenario);
        variables = new HashMap<>();
    }

    @Test
    @DisplayName("Given we can cook and ready cook" +
            "When process start then process successful")
    public void happyPathTest() throws Exception {
        schemeObject
                .stubDelegate(checkIngredients, Map.of(Constants.INGREDIENTS_AVAILABLE, true))
                .stubDelegate(letUsCook, Map.of(Constants.IS_IT_COOKING, true))
                .sendGatewayEventTrigger("IsItReady", "IsReady")
                .stubDelegate(letUsEat, Map.of(Constants.DID_WE_EAT_NOODLES, true));

        Scenario handler = Scenario.run(processScenario).startByKey(PROCESS_KEY, variables)
                .execute();

        assertAll(
                () -> assertThat(handler.instance(processScenario)).isStarted(),
                () -> verify(processScenario).hasCompleted("Start_Process"),
                () -> verify(processScenario).hasCompleted("CheckIngredients"),

                () -> verify(processScenario).hasCompleted("CanWeCook"),
                () -> verify(processScenario).hasCompleted("LetsCook"),
                () -> verify(processScenario).hasCompleted("IsReady"),
                () -> verify(processScenario).hasCompleted("LetUsEat"),

                () -> verify(processScenario).hasCompleted("End_Process"),
                () -> assertThat(handler.instance(processScenario)).isEnded()
        );
    }

    @Test
    @DisplayName("Given we can not cook " +
            "When process start then process go to order online")
    public void unHappyPathTest() throws Exception {
        schemeObject
                .stubDelegate(checkIngredients, Map.of(Constants.INGREDIENTS_AVAILABLE, false))
                .stubDelegate(orderOnline, Map.of(
                        Constants.DID_WE_EAT_NOODLES, false,
                        Constants.ORDER_ONLINE, true
                ));

        Scenario handler = Scenario.run(processScenario).startByKey(PROCESS_KEY, variables)
                .execute();

        assertAll(
                () -> assertThat(handler.instance(processScenario)).isStarted(),
                () -> verify(processScenario).hasCompleted("Start_Process"),
                () -> verify(processScenario).hasCompleted("CheckIngredients"),

                () -> verify(processScenario).hasCompleted("OrderOnline"),

                () -> verify(processScenario).hasCompleted("End_Process"),
                () -> assertThat(handler.instance(processScenario)).isEnded()
        );
    }

    @Test
    @DisplayName("Given we can cook and forgot to check" +
            "When process start then process go to order online")
    public void forgotToCheckPathTest() throws Exception {
        schemeObject
                .stubDelegate(checkIngredients, Map.of(Constants.INGREDIENTS_AVAILABLE, true))
                .stubDelegate(letUsCook, Map.of(Constants.IS_IT_COOKING, true))
                .sendGatewayTimeout("IsItReady")
                .stubDelegate(orderOnline, Map.of(
                        Constants.DID_WE_EAT_NOODLES, false,
                        Constants.ORDER_ONLINE, true
                ));

        Scenario handler = Scenario.run(processScenario).startByKey(PROCESS_KEY, variables)
                .execute();

        assertAll(
                () -> assertThat(handler.instance(processScenario)).isStarted(),
                () -> verify(processScenario).hasCompleted("Start_Process"),
                () -> verify(processScenario).hasCompleted("CheckIngredients"),

                () -> verify(processScenario).hasCompleted("OrderOnline"),

                () -> verify(processScenario).hasCompleted("End_Process"),
                () -> assertThat(handler.instance(processScenario)).isEnded()
        );
    }
}
