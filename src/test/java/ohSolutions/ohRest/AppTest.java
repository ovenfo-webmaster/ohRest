package ohSolutions.ohRest;

import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
        MockitoAnnotations.initMocks(this);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    @Mock
    HttpServletRequest request;
 
    @Mock
    HttpServletResponse response;
    
    public void testApp() throws IOException, ServletException
    {
    	
    	System.out.println("holas v2");
    }
    
    /*

    public void testApp() throws IOException, ServletException
    {
    	
    	System.out.println("holas v2");

        when(request.getParameter("fn")).thenReturn("Vinod");
        when(request.getParameter("ln")).thenReturn("Kashyap");
 
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
         
        when(response.getWriter()).thenReturn(pw);
 
        MyServlet myServlet =new MyServlet();
        myServlet.doGet(request, response);
        String result = sw.getBuffer().toString().trim();
        assertEquals(result, new String("Full Name: Vinod Kashyap"));
        System.out.println(result);
        System.out.println("finalizo");
        assertTrue( true );
    }
    

    public void testJpo() throws IOException, ServletException {
    	
    	System.out.println("holas v3");

        when(request.getHeader("Authorization")).thenReturn("Bearer a8076c32defed6e032ca8f32c0e27db2");
        
        String json = "{\"ADM\":{\"F\":{\"tipo_rol_id\":30594}}}";
        when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn( new BufferedReader(new StringReader(json)));
        
        when(request.getContentType()).thenReturn("application/json");
        when(request.getCharacterEncoding()).thenReturn("UTF-8");
        when(request.getPathInfo()).thenReturn("ADMEmpresaServiceImp/gesempresaListarRol");
        when(request.getRemoteAddr()).thenReturn("12.2.12");
        
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
         
        when(response.getWriter()).thenReturn(pw);
 
        ADMEmpresaService myServlet =new ADMEmpresaService();
        myServlet.doGet(request, response);
        
        String result = sw.getBuffer().toString().trim();

        System.out.println(result);
        System.out.println("finalizo");
        assertTrue( true );
    }
    */
}
