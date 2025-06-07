package org.springframework.core.annotation;

import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Pamirs Annotation Utils
 * <p>
 * Using {@link AnnotationUtils#getAnnotation} and {@link AnnotationUtils#findAnnotation} combination support cglib class find annotation.
 * </p>
 *
 * @author Adamancy Zhang at 18:17 on 2024-04-02
 */
public class PamirsAnnotationUtils {

    @Nullable
    public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        // Shortcut: directly present on the element, with no merging needed?
        if (AnnotationFilter.PLAIN.matches(annotationType) ||
                AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotatedElement)) {
            return annotatedElement.getAnnotation(annotationType);
        }
        // Exhaustive retrieval of merged annotations...
        return MergedAnnotations.from(annotatedElement, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none())
                .get(annotationType).withNonMergedAttributes()
                .synthesize(PamirsAnnotationUtils::isSingleAggregatePresent).orElse(null);
    }

    /**
     * <ul>
     *     <li>plain class single aggregate index is 0</li>
     *     <li>cglib proxy class single aggregate index is 5</li>
     * </ul>
     */
    private static <A extends Annotation> boolean isSingleAggregatePresent(MergedAnnotation<A> mergedAnnotation) {
        int aggregateIndex = mergedAnnotation.getAggregateIndex();
        return aggregateIndex == 0 || aggregateIndex == 5;
    }
}
