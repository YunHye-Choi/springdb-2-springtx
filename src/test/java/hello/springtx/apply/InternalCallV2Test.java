package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV2Test {
    @Autowired CallService callService;
    @Autowired InternalService internalService;

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass())    ;
        log.info("internalService class={}", internalService.getClass())    ;
    }

    @Test
    void internalCallV2() {
        internalService.internal();
    }

    @Test
    void externalCallV2() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {
        @Bean
        CallService callService() {
            return new CallService(internalCallService());
        }
        @Bean
        InternalService internalCallService() {
            return new InternalService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService {

        private final InternalService internalService;

        /**
         * internal 에서만 트랜잭션 적용되면 되는 상황이라 가정
         */
        public void external() {
            log.info("call external method");
            printTxInfo();
            internalService.internal();
        }

        private void printTxInfo() {
            boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active: {}", actualTransactionActive);
        }
    }
    static class InternalService{

        /**
         * 트랜잭션 적용 부분적으로 필요한 영역이라 가정
         * - V2 에서는 별도의 클래스로 분리! 내부호출이 아니도록 변경
         */
        @Transactional
        public void internal() {
            log.info("call internal method");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active: {}", actualTransactionActive);
        }
    }
}
