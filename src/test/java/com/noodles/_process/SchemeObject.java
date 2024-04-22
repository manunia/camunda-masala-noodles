package com.noodles._process;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.scenario.ProcessScenario;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Класс, где хранятся методы взаимодействия с элементами схемы.
 */
@RequiredArgsConstructor
public class SchemeObject {

    /**
     * Нужен для старта процесса
     */
    private final ProcessScenario processScenario;

    /**
     * Стабирует делегат.
     * @param delegate - делегат
     * @param variables - переменные контекста
     * @return текущий экземпляр
     * @throws Exception
     */
    public SchemeObject stubDelegate(final JavaDelegate delegate,
                                     final Map<String, Object> variables)
            throws Exception {
        doAnswer(invocationOnMock -> {
            DelegateExecution execution = invocationOnMock.getArgument(0);
            execution.setVariables(variables);
            return null;
        }).when(delegate).execute(any(DelegateExecution.class));
        return this;
    }

    /**
     * Стабирует событие на гейтвее.
     * @param gateway - id гейтвея
     * @param event - id события
     * @return текущий экземпляр
     */
    public SchemeObject sendGatewayEventTrigger(final String gateway, final String event) {
        when(processScenario.waitsAtEventBasedGateway(gateway))
                .thenReturn(gw -> gw.getEventSubscription(event).receive());
        return this;
    }

    /**
     * Стабирует событие на гейтвее, ожидающем time out error.
     * @param gateway - id гейтвея
     * @return текущий экземпляр
     */
    public SchemeObject sendGatewayTimeout(final String gateway) {
        when(processScenario.waitsAtEventBasedGateway(gateway))
                .thenReturn(gw -> {});
        return this;
    }
}
