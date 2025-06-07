package pro.shushi.pamirs.meta.dsl.test;

import org.springframework.stereotype.Component;

@Component
public class MockClazz {

    public String shot(String a, String b) {
        System.out.println("i'm hacking!");
        return "shot";
    }
}
