package org.toop.framework.asset.types;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.toop.framework.asset.ResourceLoader;
import org.toop.framework.asset.resources.BaseResource;

/**
 * Annotation to declare which file extensions a {@link BaseResource} subclass can handle.
 *
 * <p>This annotation is processed by the {@link ResourceLoader} to automatically register resource
 * types for specific file extensions. Each extension listed will be mapped to the annotated
 * resource class, allowing the loader to instantiate the correct type when scanning files.
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * @FileExtension({"png", "jpg"})
 * public class ImageAsset extends BaseResource implements LoadableResource {
 *     ...
 * }
 * }</pre>
 *
 * <p>Key points:
 *
 * <ul>
 *   <li>The annotation is retained at runtime for reflection-based registration.
 *   <li>Can only be applied to types (classes) that extend {@link BaseResource}.
 *   <li>Multiple extensions can be specified in the {@code value()} array.
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FileExtension {
    /**
     * The list of file extensions (without leading dot) that the annotated resource class can
     * handle.
     *
     * @return array of file extensions
     */
    String[] value();
}
