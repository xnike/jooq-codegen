/* Copyright (c) 2020 Nikolay Khramchenkov <xnike@xnike.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xnike.jooq.codegen;

import org.jooq.FilePattern;
import org.jooq.meta.jaxb.Configuration;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.Math.max;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.tools.Diagnostic.Kind.WARNING;
import static me.xnike.jooq.codegen.GenerationToolRunnerProcessor.BASE_DIR;
import static me.xnike.jooq.codegen.GenerationToolRunnerProcessor.CONFIGURATION_FILE_NAME;
import static me.xnike.jooq.codegen.Tools.getFilePatternBasedir;
import static me.xnike.jooq.codegen.Tools.getFilePatternPattern;
import static org.jooq.codegen.GenerationTool.generate;
import static org.jooq.codegen.GenerationTool.load;

@SupportedAnnotationTypes("me.xnike.jooq.codegen.GenerationToolRunnerMarker")
@SupportedOptions({BASE_DIR, CONFIGURATION_FILE_NAME})
@SupportedSourceVersion(RELEASE_8)
public class GenerationToolRunnerProcessor extends AbstractProcessor {

    /**
     *
     */
    public static final String CONFIGURATION_FILE_NAME = "generationToolRunner.configurationFile";
    /**
     *
     */
    public static final String BASE_DIR = "generationToolRunner.baseDir";

    private String baseDir;
    private String destinationDir;
    private String[] configurationFileNames;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        baseDir = processingEnv.getOptions().get(BASE_DIR);
        configurationFileNames = getConfigurationFileNames(processingEnv.getOptions().get(CONFIGURATION_FILE_NAME));

        try {
            FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", "" + System.nanoTime());
            destinationDir = new File(fileObject.getName()).getParent();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            processingEnv.getMessager().printMessage(WARNING, throwable.getMessage());
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(GenerationToolRunnerMarker.class)) {
            if (CLASS == annotatedElement.getKind()) {
                for (String configurationFileName : configurationFileNames) {
                    try (InputStream in = new FileInputStream(new File(configurationFileName))) {
                        Configuration configuration = load(in);
                        configuration.setBasedir(baseDir);
                        configuration.getGenerator().getTarget().setDirectory(destinationDir);
                        generate(configuration);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        }

        return true;
    }

    private String[] getConfigurationFileNames(String configurationFileName) {
        configurationFileName = configurationFileName.replaceAll("\\\\", "/");
        int firstOccurrence = max(configurationFileName.indexOf('*'), configurationFileName.indexOf('?'));

        if (0 > firstOccurrence) {
            return new String[]{configurationFileName};
        }

        List<String> configurationFileNames = new ArrayList<>();
        new FilePattern()
                .basedir(new File(getFilePatternBasedir(configurationFileName)))
                .pattern(getFilePatternPattern(configurationFileName))
                .load(source -> configurationFileNames.add(source.toString().replace("Source (", "").replace(")", "")));

        return configurationFileNames.toArray(new String[0]);
    }
}