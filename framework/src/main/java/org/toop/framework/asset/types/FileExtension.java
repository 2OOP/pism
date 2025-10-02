package org.toop.framework.asset.types;

import org.toop.framework.asset.ResourceLoader;
import org.toop.framework.asset.resources.BaseResource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation to declare which file extensions a {@link BaseResource} subclass
 * can handle.
 *
 * <p>This annotation is processed by the {@link ResourceLoader}
 * to automatically register resource types for specific file extensions.
 * Each extension listed will be mapped to the annotated resource class,
 * allowing the loader to instantiate the correct type when scanning files.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @FileExtension({"png", "jpg"})
 * public class ImageAsset extends BaseResource implements LoadableResource {
 *     ...
 * }
 * }</pre>
 *
 * <p>Key points:</p>
 * <ul>
 *     <li>The annotation is retained at runtime for reflection-based registration.</li>
 *     <li>Can only be applied to types (classes) that extend {@link BaseResource}.</li>
 *     <li>Multiple extensions can be specified in the {@code value()} array.</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FileExtension {
    /**
     * The list of file extensions (without leading dot) that the annotated resource class can handle.
     *
     * @return array of file extensions
     */
    String[] value();
}
