import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CacheControlFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Blog posts ke liye short cache
        httpResponse.setHeader("Cache-Control", "no-cache, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        
        chain.doFilter(request, response);
    }
}