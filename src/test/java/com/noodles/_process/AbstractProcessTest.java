package com.noodles._process;

import com.noodles.workflow.delegates.CheckIngredients;
import com.noodles.workflow.delegates.LetUsCook;
import com.noodles.workflow.delegates.LetUsEat;
import com.noodles.workflow.delegates.OrderOnline;
import org.camunda.bpm.scenario.ProcessScenario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

/**
 * Abstract base test class.
 */
@SpringBootTest
@ActiveProfiles("bpm-process-test")
public class AbstractProcessTest {

    /**
     * Интерфейс для освобождения ресурсов.
     */
    private AutoCloseable closeable;

    /**
     * Мокаем сценарий через api плаформы Camunda.
     */
    @Mock
    protected ProcessScenario processScenario;

    /**
     * Внедряем нужные нам зависимости делегатов спрингового приложения.
     */
    @SpyBean
    protected CheckIngredients checkIngredients;

    @SpyBean
    protected LetUsCook letUsCook;

    @SpyBean
    protected LetUsEat letUsEat;

    @SpyBean
    protected OrderOnline orderOnline;

    /**
     * Переменные контекста.
     */
    protected Map<String, Object> variables;

    /**
     * Предусловия тестов.
     */
    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        init();
    }

    /**
     * Постусловия тестов.
     */
    @AfterEach
    void tearDown() {
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void init() {
    }
}
