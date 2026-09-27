package infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves every REST controller under /api, so that the paths of the API never collide with the
 * paths of the screens (reloading /clubs/new must show the screen, not call the API).
 */
@Configuration
class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer paths) {
        paths.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class));
    }
}
