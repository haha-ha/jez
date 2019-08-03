package jez.daogen.generator;

import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class GeneratorTest {
    @Autowired
    Generator generator;

    @Test
    public void testGenerateOneTable() throws Exception{
        generator.generate("pilot", "TestAll", "jez.app.dao", "/devl/projects/java/jez/DaoGen/src/test/java");
    }
}