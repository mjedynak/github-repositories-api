package pl.mjedynak.api;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    @Test
    public void noLayerViolations() {
        JavaClasses classes = new ClassFileImporter().importPackages("pl.mjedynak.api");

        Architectures.LayeredArchitecture architecture = layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Application")
                .definedBy("..application..")
                .layer("Domain")
                .definedBy("..domain..")
                .layer("Infrastructure")
                .definedBy("..infrastructure..")
                // constraints
                .whereLayer("Infrastructure")
                .mayNotBeAccessedByAnyLayer()
                .whereLayer("Infrastructure")
                .mayOnlyAccessLayers("Domain")
                .whereLayer("Domain")
                .mayNotAccessAnyLayer()
                .whereLayer("Application")
                .mayNotBeAccessedByAnyLayer()
                .whereLayer("Application")
                .mayOnlyAccessLayers("Domain");

        architecture.check(classes);
    }
}
