package me.xnike.jooq.codegen;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;

import static java.nio.file.Files.exists;
import static javax.lang.model.element.ElementKind.CLASS;
import static me.xnike.jooq.codegen.GenerationToolRunnerProcessor.BASE_DIR;
import static me.xnike.jooq.codegen.GenerationToolRunnerProcessor.CONFIGURATION_FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GenerationToolRunnerProcessorTest {

    @TempDir
    Path tempDirectory;

    @Mock
    private ProcessingEnvironment processingEnvironment;

    @Mock
    private RoundEnvironment roundEnvironment;

    @Test
    public void configurationFile1() {
        configure("src/test/resources", "src/test/resources/configuration1.xml", tempDirectory.toString());

        GenerationToolRunnerProcessor processor = new GenerationToolRunnerProcessor();
        processor.init(processingEnvironment);
        processor.process(null, roundEnvironment);

        assertTrue(exists(Paths.get(tempDirectory.toString(), "me/xnike/jooq/codegen/schema1/Schema1.java")));
        assertFalse(exists(Paths.get(tempDirectory.toString(), "me/xnike/jooq/codegen/schema2/Schema2.java")));
    }

    @Test
    public void configurationFile1_2() {
        configure("src/test/resources", "src/test/resources/configuration*.xml", tempDirectory.toString());

        GenerationToolRunnerProcessor processor = new GenerationToolRunnerProcessor();
        processor.init(processingEnvironment);
        processor.process(null, roundEnvironment);

        assertTrue(exists(Paths.get(tempDirectory.toString(), "me/xnike/jooq/codegen/schema1/Schema1.java")));
        assertTrue(exists(Paths.get(tempDirectory.toString(), "me/xnike/jooq/codegen/schema2/Schema2.java")));
    }

    @Test
    public void configurationFile2() {
        configure("src/test/resources", "src/test/resources/configuration2.xml", tempDirectory.toString());

        GenerationToolRunnerProcessor processor = new GenerationToolRunnerProcessor();
        processor.init(processingEnvironment);
        processor.process(null, roundEnvironment);

        assertFalse(exists(Paths.get(tempDirectory.toString(), "me/xnike/jooq/codegen/schema1/Schema1.java")));
        assertTrue(exists(Paths.get(tempDirectory.toString(), "me/xnike/jooq/codegen/schema2/Schema2.java")));
    }

    private void configure(String baseDir, String configurationFileName, String targetDirectory) {
        when(processingEnvironment.getFiler()).thenAnswer(invocationOnMock -> {
            Filer filer = mock(Filer.class);
            when(filer.createResource(any(), any(), any(), any())).thenAnswer(invocation -> {
                FileObject fileObject = mock(FileObject.class);
                when(fileObject.getName()).thenReturn(Paths.get(targetDirectory, (String) invocation.getArgument(2)).toString());
                return fileObject;
            });
            return filer;
        });
        when(processingEnvironment.getOptions()).thenReturn(new HashMap<String, String>() {{
            put(BASE_DIR, baseDir);
            put(CONFIGURATION_FILE_NAME, configurationFileName);
        }});
        when(roundEnvironment.getElementsAnnotatedWith(GenerationToolRunnerMarker.class)).thenAnswer(invocation -> {
            Element element = mock(Element.class);
            when(element.getKind()).thenReturn(CLASS);
            return Collections.singleton(element);
        });
    }
}