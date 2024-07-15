package hello.springtx.apply;

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
public class InternalCallV1Test {
    @Autowired CallService callService;

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass())    ;
    }

    @Test
    void internalCall() {
        callService.internal();
    }

    @Test
    void externalCall() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {
        @Bean
        CallService callService() {
            return new CallService();
        }
    }


    static class CallService {

        /**
         * internal 에서만 트랜잭션 적용되면 되는 상황이라 가정
         */
        public void external() {
            log.info("call external method");
            printTxInfo();
            internal();
        }

        /**
         * 트랜잭션 적용 부분적으로 필요한 영역이라 가정
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
