package io.github.ppp16bit.message_broker.clj;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import org.springframework.stereotype.Component;

import io.github.ppp16bit.message_broker.model.Message;

@Component
public class ClojureBridge {
    private final IFn processFn;

    public ClojureBridge() {
        Clojure.var("clojure.core", "require")
                .invoke(Clojure.read("broker.pipeline"));
        processFn = Clojure.var("broker.pipeline", "process");
    }

    public void process(Message message) {
        processFn.invoke(message);
    }
}